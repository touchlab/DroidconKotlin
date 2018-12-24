package co.touchlab.sessionize

import co.touchlab.sessionize.db.QueryUpdater
import com.squareup.sqldelight.Query
import kotlin.coroutines.CoroutineContext

abstract class BaseQueryModelView<Q:Any, VT>(
        query: Query<Q>,
        extractData:(Query<Q>)->VT,
        mainContext: CoroutineContext):BaseModel(mainContext){

    private val updater: QueryUpdater<Q, VT> = QueryUpdater(query, {extractData(it)}){viewData ->
        view?.let {
            it.update(viewData)
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
        fun update(data:VT)
    }
}