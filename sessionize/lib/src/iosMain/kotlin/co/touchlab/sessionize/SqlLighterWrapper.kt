package co.touchlab.sessionize

import co.touchlab.sqliter.*
import co.touchlab.stately.collections.frozenHashMap
import co.touchlab.stately.collections.frozenLinkedList
import co.touchlab.stately.concurrency.AtomicBoolean
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.concurrency.Lock
import co.touchlab.stately.concurrency.withLock
import co.touchlab.stately.freeze
import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDatabaseConnection
import com.squareup.sqldelight.db.SqlPreparedStatement

class SQLiterConnection(
        private val databaseConnection: DatabaseConnection
) : SqlDatabaseConnection {
    private val enforceClosed = EnforceClosed()
    private val transaction: AtomicReference<Transaction?> = AtomicReference(null)
    private val transLock = Lock()
    private val statementList = frozenLinkedList<Statement>(stableIterator = false)
    private val queryList = frozenLinkedList<SQLiterQuery>(stableIterator = false)

    override fun currentTransaction(): Transacter.Transaction? = transaction.value

    override fun newTransaction(): Transacter.Transaction =
            transLock.withLock {
                val enclosing = transaction.value
                val trans = Transaction(enclosing).freeze()
                transaction.value = trans

                if (enclosing == null) {
                    databaseConnection.beginTransaction()
                }

                return trans
            }

    override fun prepareStatement(
            sql: String,
            type: SqlPreparedStatement.Type,
            parameters: Int
    ): SqlPreparedStatement {
        enforceClosed.checkNotClosed()

        return when (type) {
            SqlPreparedStatement.Type.SELECT -> {
                SQLiterQuery(sql, databaseConnection).apply { queryList.add(this) }
            }
            SqlPreparedStatement.Type.INSERT, SqlPreparedStatement.Type.UPDATE, SqlPreparedStatement.Type.DELETE, SqlPreparedStatement.Type.EXECUTE -> {
                val statement = databaseConnection.createStatement(sql)
                statementList.add(statement)
                SQLiterStatement(statement)
            }
        }
    }

    internal fun close(closeDbConnection: Boolean = true) {
        enforceClosed.checkNotClosed()
        enforceClosed.trackClosed()
        statementList.forEach { it.finalizeStatement() }
        queryList.forEach { it.close() }
        if (closeDbConnection) {
            databaseConnection.close()
        }
    }

    inner class Transaction(
            override val enclosingTransaction: Transaction?
    ) : Transacter.Transaction() {

        override fun endTransaction(successful: Boolean) = transLock.withLock {
            if (enclosingTransaction == null) {
                if (successful) {
                    databaseConnection.setTransactionSuccessful()
                    databaseConnection.endTransaction()
                } else {
                    databaseConnection.endTransaction()
                }
            }
            transaction.value = enclosingTransaction
        }
    }
}

private class SQLiterQuery(
        private val sql: String,
        private val database: DatabaseConnection
) : SqlPreparedStatement {
    private val availableStatements = frozenLinkedList<Statement>(stableIterator = false)
    private val allStatements = frozenLinkedList<Statement>(stableIterator = false)
    private val enforceClosed = EnforceClosed()
    private val queryLock = Lock()
    private val binds = frozenHashMap<Int, (Statement) -> Unit>()

    internal fun close() {
        queryLock.withLock {
            enforceClosed.checkNotClosed()
            enforceClosed.trackClosed()
            allStatements.forEach {
                it.finalizeStatement()
            }
        }
    }

    override fun bindBytes(
            index: Int,
            bytes: ByteArray?
    ) {
        bytes.freeze()
        binds.put(index) { it.bindBlob(index, bytes) }
    }

    override fun bindDouble(
            index: Int,
            double: Double?
    ) {
        binds.put(index) { it.bindDouble(index, double) }
    }

    override fun bindLong(
            index: Int,
            long: Long?
    ) {
        binds.put(index) { it.bindLong(index, long) }
    }

    override fun bindString(
            index: Int,
            string: String?
    ) {
        binds.put(index) { it.bindString(index, string) }
    }

    private fun bindTo(statement: Statement) {
        for (bind in binds.values.iterator()) {
            bind(statement)
        }
    }

    override fun execute() = throw UnsupportedOperationException()

    private fun findStatement(): Statement = queryLock.withLock {
        enforceClosed.checkNotClosed()
        return if (availableStatements.size == 0) {
            val statement = database.createStatement(sql)
            allStatements.add(statement)
            statement
        } else {
            availableStatements.removeAt(0)
        }
    }

    internal fun cacheStatement(statement: Statement) {
        queryLock.withLock {
            enforceClosed.checkNotClosed()
            statement.resetStatement()
            availableStatements.add(statement)
        }
    }

    override fun executeQuery(): SqlCursor {
        val stmt = findStatement()
        bindTo(stmt)
        return SQLiterCursorWrapper(stmt, this)
    }
}

private class SQLiterStatement(
        private val statement: Statement
) : SqlPreparedStatement {

    override fun bindBytes(
            index: Int,
            bytes: ByteArray?
    ) {
        bytes.freeze()
        statement.bindBlob(index, bytes)
    }

    override fun bindDouble(
            index: Int,
            double: Double?
    ) {
        statement.bindDouble(index, double)
    }

    override fun bindLong(
            index: Int,
            long: Long?
    ) {
        statement.bindLong(index, long)
    }

    override fun bindString(
            index: Int,
            string: String?
    ) {
        statement.bindString(index, string)
    }

    override fun execute() {
        statement.execute()
    }

    override fun executeQuery(): SqlCursor = throw UnsupportedOperationException()
}

private class SQLiterCursorWrapper(
        private val statement: Statement,
        private val query: SQLiterQuery
) : SqlCursor {
    private val cursor = statement.query()

    override fun close() {
        query.cacheStatement(statement)
    }

    override fun getBytes(index: Int): ByteArray? = cursor.getBytesOrNull(index)

    override fun getDouble(index: Int): Double? = cursor.getDoubleOrNull(index)

    override fun getLong(index: Int): Long? = cursor.getLongOrNull(index)

    override fun getString(index: Int): String? = cursor.getStringOrNull(index)

    override fun next(): Boolean = cursor.next()
}

private class EnforceClosed {
    private val closed = AtomicBoolean(false)

    fun trackClosed() {
        closed.value = true
    }

    fun checkNotClosed() {
        if (closed.value)
            throw IllegalStateException("Closed")
    }
}