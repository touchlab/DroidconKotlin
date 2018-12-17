package co.touchlab.sessionize

class EventViewModel(sessionId: String){
    val eventModel = EventModel(sessionId)

    fun registerForChanges(proc:(sessionInfo:SessionInfo, formattedRoomTime:String)->Unit){
        eventModel.register(object : EventModel.View{
            override fun update(sessionInfo: SessionInfo, formattedRoomTime: String) {
                proc(sessionInfo, formattedRoomTime)
            }
        })
    }

    fun toggleRsvp(rsvp:Boolean){
        eventModel.toggleRsvp(rsvp)
    }

    fun unregister(){
        eventModel.shutDown()
    }
}

