package co.touchlabYeah.sessionize

import co.touchlab.sessionize.EventModel
import co.touchlab.sessionize.SessionInfo
import co.touchlab.sessionize.formattedRoomTime

class EventViewModel(sessionId: String) {
    val eventModel = EventModel(sessionId)

    fun registerForChanges(proc: (sessionInfo: SessionInfo, formattedRoomTime: String) -> Unit) {
        eventModel.register(object : EventModel.EventView {
            override suspend fun update(data: SessionInfo) {
                proc(data, data.session.formattedRoomTime())
            }
        })
    }

    fun toggleRsvp(event: SessionInfo) {
        eventModel.toggleRsvp(event)
    }

    fun unregister() {
        eventModel.shutDown()
    }
}

