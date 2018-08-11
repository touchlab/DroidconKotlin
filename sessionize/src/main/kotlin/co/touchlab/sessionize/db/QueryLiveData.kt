package co.touchlab.sessionize.db

import co.touchlab.multiplatform.architecture.livedata.MutableLiveData
import co.touchlab.sessionize.platform.backgroundTask
import com.squareup.sqldelight.Query

abstract class QueryLiveData<Q:Any, Z>(val q: Query<Q>, skipInit:Boolean = false) : MutableLiveData<Z>(), Query.Listener {
    init {
        q.addListener(listener = this)

        if(!skipInit) {
            backgroundTask {
                queryResultsChanged()
            }
        }
    }

    /**
     * You need to call this manually because of https://youtrack.jetbrains.com/issue/KT-19848
     *
     * We can't typealias 'onActive' and 'onInactive' in LiveData because they're protected. For future versions
     * it might make sense to create a whole different type hierarchy that delegates to LiveData, but some thinking
     * needs to happen around that.
     */
    fun removeListener() {
        q.removeListener(this)
    }

    abstract fun extractData(q: Query<Q>): Z

    override fun queryResultsChanged() {
        println("queryResultsChanged: Value changed")
        postValue(extractData(q))
    }
}