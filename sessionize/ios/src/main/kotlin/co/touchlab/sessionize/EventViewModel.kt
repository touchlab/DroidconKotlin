package co.touchlab.sessionize

import co.touchlab.multiplatform.architecture.livedata.*
import kotlin.native.*
import kotlin.native.concurrent.*

class EventViewModel(sessionId: String){
    val eventModel = EventModel(sessionId).freeze()
    var eventObserver:Observer<SessionInfo>? = null

    fun registerForChanges(proc:(sessionInfo:SessionInfo)->Unit){
        eventObserver = object : Observer<SessionInfo>{
            override fun onChanged(t: SessionInfo?){
                if(t != null)
                    proc(t)
            }
        }

        eventModel.evenLiveData.observeForever(eventObserver!!)
    }

    fun toggleRsvp(rsvp:Boolean){
        eventModel.toggleRsvp(rsvp)
    }

    fun unregister(){
        eventModel.evenLiveData.removeObserver(eventObserver!!)
        eventObserver = null
        eventModel.shutDown()
    }
}