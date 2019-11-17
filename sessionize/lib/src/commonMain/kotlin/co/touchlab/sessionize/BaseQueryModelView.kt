package co.touchlab.sessionize

import co.touchlab.sessionize.platform.assertNotMainThread
import co.touchlab.sessionize.platform.printThrowable
import co.touchlab.stately.ensureNeverFrozen
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Sort of a "controller" in MVC thinking. Pass in the SQLDelight Query,
 * a method to actually extract data on updates. The View interface is
 * generically defined to take data extracted from the Query, and manage
 * registering and shutting down.
 */
@UseExperimental(InternalCoroutinesApi::class)
abstract class BaseQueryModelView<Q : Any, VT>(
        query: Query<Q>,
        extractData: (Query<Q>) -> VT,
        mainContext: CoroutineContext) : BaseModel(mainContext) {

    init {
        ensureNeverFrozen()
        mainScope.launch {
            query.asFlow()
                    .map {
                        assertNotMainThread()
                        extractData(it)
                    }
                    .flowOn(ServiceRegistry.backgroundDispatcher)
                    .collect { vt ->
                        view?.let {
                            it.update(vt)
                        }
                    }

        }
    }

    private var view: View<VT>? = null

    fun register(view: View<VT>) {
        this.view = view
    }

    fun shutDown() {
        view = null
    }

    interface View<VT> {
        suspend fun update(data: VT)
        fun error(t:Throwable){
            printThrowable(t)
            ServiceRegistry.softExceptionCallback(t, t.message?:"(Unknown View Error)")
        }
    }
}