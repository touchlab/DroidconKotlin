package co.touchlab.sessionize

import co.touchlab.sessionize.db.QueryUpdater
import com.squareup.sqldelight.Query
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Sort of a "controller" in MVC thinking. Pass in the SQLDelight Query,
 * a method to actually extract data on updates. The View interface is
 * generically defined to take data extracted from the Query, and manage
 * registering and shutting down.
 */
abstract class BaseQueryModelView<Q:Any, VT>(
        query: Query<Q>,
        extractData:(Query<Q>)->VT,
        mainContext: CoroutineContext):BaseModel(mainContext){

    private val updater: QueryUpdater<Q, VT> = QueryUpdater(query, {extractData(it)}){viewData ->
        view?.let {
            launch {
                it.update(viewData)
            }
        }
    }

    private var view:View<VT>? = null

    fun register(view: View<VT>){
        this.view = view
        updater.refresh()
    }

    fun shutDown(){
        view = null
        updater.destroy()
    }

    interface View<VT>{
        suspend fun update(data:VT)
    }
}