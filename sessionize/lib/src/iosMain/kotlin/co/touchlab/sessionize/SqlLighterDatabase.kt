package co.touchlab.sessionize

import co.touchlab.sqliter.*
import co.touchlab.stately.collections.AbstractSharedLinkedList
import co.touchlab.stately.collections.SharedLinkedList
import co.touchlab.stately.collections.frozenHashMap
import co.touchlab.stately.collections.frozenLinkedList
import co.touchlab.stately.concurrency.*
import co.touchlab.stately.freeze
import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDatabase
import com.squareup.sqldelight.db.SqlDatabaseConnection
import com.squareup.sqldelight.db.SqlPreparedStatement

class SqlLighterDatabase(private val databaseManager: DatabaseManager) : SqlDatabase {
    /*private val connectionInstanceThreadCache = frozenLinkedList<ThreadConnection>()
    private val myThread = ThreadLocalRef<ThreadConnection>()*/

    internal val connectionCache = ThreadLocalCache<ThreadConnection> {
        ThreadConnection(databaseManager.createMultiThreadedConnection(), this)
    }

    /**
     * Keep all outstanding cursors to close when closing the db, just in case the user didn't.
     */
    internal val cursorCollection = frozenLinkedList<Cursor>() as SharedLinkedList<Cursor>

    private val connectionLock = Lock()
    private val singleOpConnection = databaseManager.createMultiThreadedConnection()
    private val publicApiConnection = SqlLighterConnection(this)

    override fun close() = connectionLock.withLock {
        connectionCache.clear { it.connection.close() }
        singleOpConnection.close()
    }

    override fun getConnection(): SqlDatabaseConnection = publicApiConnection

    /**
     * If we're in a transaction, then I have a connection. Otherwise we lock and
     * use the open connection on which all other ops run.
     */
    internal fun <R> realConnectionMineOrUnaligned(block: DatabaseConnection.() -> R): R {
        val mine = connectionCache.mineOrNone()
        return if (mine != null)
        {
            mine.connection.block()
        }
        else {
            connectionLock.withLock {
                singleOpConnection.block()
            }
        }
    }

    internal fun recycleCursor(node: AbstractSharedLinkedList.Node<Cursor>) = connectionLock.withLock {
        node.nodeValue.statement.finalizeStatement()
        node.remove()
    }

    internal fun trackCursor(cursor: Cursor):AbstractSharedLinkedList.Node<Cursor> = connectionLock.withLock {
        cursorCollection.addNode(cursor)
    }
}

fun wrapConnection(
        connection: DatabaseConnection,
        block: (SqlDatabaseConnection) -> Unit
) {
    val conn = SQLiterConnection(connection)
    try {
        block(conn)
    } finally {
        conn.close(closeDbConnection = false)
    }
}

class SqlLighterConnection(private val database: SqlLighterDatabase) : SqlDatabaseConnection {
    override fun currentTransaction(): Transacter.Transaction? = database.connectionCache.mineOrNone()?.transaction?.value

    override fun newTransaction(): Transacter.Transaction {
        println("newTransaction a")
        val myConn = database.connectionCache.mineOrAlign()
        println("newTransaction b")
        return myConn.newTransaction()
    }

    override fun prepareStatement(sql: String, type: SqlPreparedStatement.Type, parameters: Int): SqlPreparedStatement =
            SqlLighterStatement(sql, type, database)
}

class ThreadConnection(val connection: DatabaseConnection, private val sqlLighterDatabase: SqlLighterDatabase) {
    internal val transaction: AtomicReference<Transaction?> = AtomicReference(null)

    fun newTransaction(): Transaction {
        val enclosing = transaction.value

        //Create here, in case we bomb...
        if (enclosing == null) {
            connection.beginTransaction()
        }

        val trans = Transaction(enclosing).freeze()
        transaction.value = trans

        return trans
    }

    inner class Transaction(override val enclosingTransaction: Transaction?) : Transacter.Transaction() {

        override fun endTransaction(successful: Boolean) {
            if (enclosingTransaction == null) {
                try {
                    if (successful) {
                        connection.setTransactionSuccessful()
                    }

                    connection.endTransaction()
                } finally {
                    sqlLighterDatabase.connectionCache.mineRelease()
                }

            }
            transaction.value = enclosingTransaction
        }
    }
}

class SqlLighterStatement(
        private val sql: String,
        private val type: SqlPreparedStatement.Type,
        private val database: SqlLighterDatabase
) : SqlPreparedStatement {

    private val statementCache = ThreadLocalCache {ThreadStatement()}

    //For each statement declared, there can be an instance per-thread.
//    private val statementInstanceThreadCache = frozenLinkedList<ThreadStatement>()
//    private val myStatementInstance = ThreadLocalRef<ThreadStatement>()

    override fun bindBytes(index: Int, value: ByteArray?) = myThreadStatementInstance().bindBytes(index, value)
    override fun bindDouble(index: Int, value: Double?) = myThreadStatementInstance().bindDouble(index, value)
    override fun bindLong(index: Int, value: Long?) = myThreadStatementInstance().bindLong(index, value)
    override fun bindString(index: Int, value: String?) = myThreadStatementInstance().bindString(index, value)

    /**
     * Executing a statement clears the instance definition. Effectively that means the bindings are reset. We can
     * recycle these rather than letting them be deallocated in the future if that improves performance in some way.
     */
    override fun execute() {
        database.realConnectionMineOrUnaligned {
            withStatement(sql) {
                applyBindings(this)
                when (type) {
                    SqlPreparedStatement.Type.SELECT -> throw kotlin.AssertionError()
                    else -> execute()
                }
            }
        }
        removeMyInstance()
    }

    /**
     * Creating a cursor returns an actual sqlite statement instance, so we need to be careful with these. However,
     * the design of sqldelight's queries and the QueryWrapper would like us to retain the bindings (I think), so
     * we leave the instance for this thread hanging out.
     *
     * If the developer creates many threads, this will grow over time. We will probably want to either cap the cache
     * count, and risk losing the bindings, or redesign the interface somewhat.
     *
     * It does seem that executable statements and queries are designed somewhat differently, however.
     */
    override fun executeQuery(): SqlCursor = database.realConnectionMineOrUnaligned {
        val statement = createStatement(sql)
        applyBindings(statement)
        SQLiterCursor(database.trackCursor(statement.query()), database)
    }

    private fun applyBindings(statement: Statement){
        statementCache.mineOrNone()?.binds?.forEach {
            it.value(statement)
        }
    }

    //These don't need to be locked because only *your* thread is adding/removing it's own entries
    //Assuming the list itself is thread safe, no problem
    private fun myThreadStatementInstance(): ThreadStatement {
//        println("myThreadStatementInstance before instance")
        val instance = statementCache.mineOrAlign()
//        println("myThreadStatementInstance $instance")
        return instance
    }

    private fun removeMyInstance() {
        statementCache.mineRelease()
    }
}

class SQLiterCursor(private val cursorCollectionNode: AbstractSharedLinkedList.Node<Cursor>, private val database: SqlLighterDatabase) : SqlCursor {
    private val cursor = cursorCollectionNode.nodeValue

    override fun close() {
        database.recycleCursor(cursorCollectionNode)
    }

    override fun getBytes(index: Int): ByteArray? = cursor.getBytesOrNull(index)

    override fun getDouble(index: Int): Double? = cursor.getDoubleOrNull(index)

    override fun getLong(index: Int): Long? = cursor.getLongOrNull(index)

    override fun getString(index: Int): String? = cursor.getStringOrNull(index)

    override fun next(): Boolean = cursor.next()
}

class ThreadLocalCache<T>(private val producer:()->T){
    private val cache = frozenLinkedList<CacheEntry<T>>()
    val localRef = ThreadLocalRef<CacheEntry<T>>()
    private val cacheLock = Lock()

    fun mineOrNone():T? = localRef.value?.entry

    fun mineOrAlign():T{
        val mine = localRef.value
        if(mine != null)
            return mine.entry

        return cacheLock.withLock {
            val unaligned = cache.find { !it.inUse.value } ?: createEntry()
            localRef.value = unaligned
            unaligned.inUse.value = true
            unaligned.entry
        }
    }

    fun mineRelease() = cacheLock.withLock {
        val myEntry = localRef.value
        if(myEntry != null){
            localRef.value = null
            myEntry.inUse.value = false
        }
    }

    private fun createEntry():CacheEntry<T>{
        val newVal = producer().freeze()
        val entry = CacheEntry(newVal)
        cache.add(entry)
        return entry
    }

    fun clear(clearBlock:(T)->Unit = {}) = cacheLock.withLock {
        cache.forEach {
            clearBlock(it.entry)
        }
        cache.clear()
    }

    class CacheEntry<T>(val entry:T){
        val inUse = AtomicBoolean(false)
    }

}

class ThreadStatement {
    internal val binds = frozenHashMap<Int, (Statement) -> Unit>()

    fun bindBytes(index: Int, value: ByteArray?) {
        binds.put(index) { it.bindBlob(index, value) }
    }

    fun bindDouble(index: Int, value: Double?) {
        binds.put(index) { it.bindDouble(index, value) }
    }

    fun bindLong(index: Int, value: Long?) {
        binds.put(index) { it.bindLong(index, value) }
    }

    fun bindString(index: Int, value: String?) {
        binds.put(index) { it.bindString(index, value) }
    }
}