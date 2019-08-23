package co.touchlab.sessionize

import co.touchlab.sessionize.architecture.MainThreadPubSub
import co.touchlab.sessionize.architecture.Sub
import co.touchlab.sessionize.db.QueryPub
import co.touchlab.sessionize.platform.printThrowable
import co.touchlab.stately.ensureNeverFrozen
import com.squareup.sqldelight.Query
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Sort of a "controller" in MVC thinking. Pass in the SQLDelight Query,
 * a method to actually extract data on updates. The View interface is
 * generically defined to take data extracted from the Query, and manage
 * registering and shutting down.
 */
abstract class BaseQueryModelView<Q : Any, VT>(
        query: Query<Q>,
        extractData: (Query<Q>) -> VT,
        mainContext: CoroutineContext) : BaseModel(mainContext) {

    private val queryPub = QueryPub(query, extractData)

    init {
        ensureNeverFrozen()
        val mainPub = MainThreadPubSub<VT>()
        queryPub.addSub(mainPub)
        mainPub.addSub(object : Sub<VT> {
            override fun onNext(next: VT) {
                view?.let {
                    launch {
                        it.update(next)
                    }
                }
            }

            override fun onError(t: Throwable) {
                view?.let { it.error(t) }
            }
        })
    }

    private var view: View<VT>? = null

    fun register(view: View<VT>) {
        this.view = view
        queryPub.refresh()
    }

    fun shutDown() {
        view = null
        queryPub.destroy()
    }

    interface View<VT> {
        suspend fun update(data: VT)
        fun error(t:Throwable){
            printThrowable(t)
            ServiceRegistry.softExceptionCallback(t, t.message?:"(Unknown View Error)")
        }
    }
}