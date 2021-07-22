package co.touchlab.sessionize

import co.touchlab.stately.ensureNeverFrozen
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

/**
 * Sort of a "controller" in MVC thinking. Pass in the SQLDelight Query,
 * a method to actually extract data on updates. The View interface is
 * generically defined to take data extracted from the Query, and manage
 * registering and shutting down.
 */
@UseExperimental(InternalCoroutinesApi::class)
abstract class BaseQueryModelView<Q : Any, VT>(
    query: Query<Q>,
    extractData: (Query<Q>) -> VT
) : BaseModel() {

    init {
        ensureNeverFrozen()
        mainScope.launch {
            query.asFlow()
                .map {
                    extractData(it)
                }
                .flowOn(backgroundDispatcher)
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
        onDestroy()
    }

    interface View<VT> : KoinComponent {
        suspend fun update(data: VT)
        fun error(t: Throwable) {
            t.printStackTrace()
            softExceptionCallback.invoke(t, t.message ?: "(Unknown View Error)")
        }
    }
}