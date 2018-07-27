package co.touchlab.notepad.db

import co.touchlab.multiplatform.architecture.threads.MutableLiveData
import co.touchlab.notepad.utils.backgroundTask
import com.squareup.sqldelight.Query

abstract class QueryLiveData<Z>(val q: Query<*>, skipInit:Boolean = false) : MutableLiveData<Z>(), Query.Listener {
    init {
        println("QueryLiveData-Before")
        q.addListener(listener = this)

        if(!skipInit) {
            backgroundTask {
                queryResultsChanged()
            }
        }
    }

    fun removeListener() {
        q.removeListener(this)
    }

    abstract fun extractData(q: Query<*>): Z

    override fun queryResultsChanged() {
        println("queryResultsChanged: Value changed")
        postValue(extractData(q))
    }
}