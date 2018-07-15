package co.touchlab.notepad

import co.touchlab.multiplatform.architecture.threads.*
import co.touchlab.notepad.data.*
import konan.worker.*

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

        eventModel.sessionInfoLiveData.observeForever(eventObserver!!)
    }

    fun unregister(){
        eventModel.sessionInfoLiveData.removeObserver(eventObserver!!)
        eventObserver = null
        eventModel.shutDown()
    }
}