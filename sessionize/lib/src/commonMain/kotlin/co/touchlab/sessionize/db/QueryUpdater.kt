package co.touchlab.sessionize.db

import co.touchlab.sessionize.lateValue
import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.ensureNeverFrozen
import co.touchlab.stately.freeze
import com.squareup.sqldelight.Query

/**
 * Listens to SQLDelight query, extracts result with extractData, and passes that to updateSource.
 *
 * The extractData function needs to be frozen, which will freeze anything it captures, but
 * updateSource stays in the main thread.
 */
class QueryUpdater<Q:Any, Z>(internal val q: Query<Q>, skipInit:Boolean = false,
                                      internal val extractData:(Query<Q>)->Z,
                                      internal val updateSource:(Z)->Unit
){
    private val internalListener = InternalListener(this).freeze()
    init {
        ensureNeverFrozen()
        q.addListener(internalListener)
    }

    internal fun internalUpdated(update:Z){
        assertMainThread()
        updateSource(update)
    }

    fun destroy(){
        q.removeListener(internalListener)
    }

    fun refresh(){
        internalListener.queryResultsChanged()
    }
}

/**
 * This class gets put into the Query listener set. It needs to be frozen, but
 * we should be careful not to freeze QueryUpdater arg.
 */
internal class InternalListener<Q:Any, Z>(q:QueryUpdater<Q, Z>):Query.Listener{
    private val extractDataJob = q.extractData.freeze()
    private val query = q.q
    private val updaterReference = ThreadLocalRef<QueryUpdater<Q, Z>>()

    private val resultJob: (Z)->Unit = {arg:Z ->
        updaterReference.lateValue.internalUpdated(arg)
    }

    init {
        updaterReference.value = q
        freeze()
    }

    override fun queryResultsChanged() {
        //Push to main thread
        backToFront({extractDataJob(query)}, resultJob)
    }
}

internal expect fun <B> backToFront(b:()->B, job: (B) -> Unit)
internal expect val mainThread:Boolean

internal fun assertMainThread(){
    if(!mainThread)
        throw IllegalStateException("Must be on main thread")
}
