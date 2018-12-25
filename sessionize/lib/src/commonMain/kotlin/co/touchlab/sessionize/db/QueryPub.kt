package co.touchlab.sessionize.db

import co.touchlab.sessionize.architecture.BasePub
import co.touchlab.sessionize.architecture.Pub
import co.touchlab.sessionize.architecture.Sub
import co.touchlab.stately.collections.frozenCopyOnWriteList
import co.touchlab.stately.concurrency.AtomicBoolean
import co.touchlab.stately.freeze
import com.squareup.sqldelight.Query

class QueryPub<Q:Any, Z>(val q: Query<Q>, val extractData:(Query<Q>)->Z):Query.Listener, BasePub<Z>() {
    private val subList = frozenCopyOnWriteList<Sub<Z>>()

    init{
        q.addListener(this)
        freeze()
    }

    fun destroy(){
        q.removeListener(this)
    }

    override fun subs(): MutableCollection<Sub<Z>> = subList

    override fun queryResultsChanged() {
        applyNext { extractData(q) }
    }

    fun refresh(){
        queryResultsChanged()
    }
}