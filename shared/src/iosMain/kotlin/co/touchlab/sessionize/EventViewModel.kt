package co.touchlabYeah.sessionize

import co.touchlab.sessionize.EventModel
import co.touchlab.sessionize.SessionInfo
import co.touchlab.sessionize.SessionRoomtimeFormatter
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.formattedRoomTime
import co.touchlab.sessionize.platform.NotificationsModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class EventViewModel(sessionId: String) : KoinComponent {
    val eventModel = EventModel(sessionId, get(), get(), get())
    val timeZone: String by inject(qualifier = named("timeZone"))
    val dbHelper: SessionizeDbHelper by inject()
    fun registerForChanges(proc: (sessionInfo: SessionInfo, formattedRoomTime: String) -> Unit) {
        eventModel.register(object : EventModel.EventView {
            override suspend fun update(data: SessionInfo) {
                proc(
                    data,
                    data.session.formattedRoomTime(SessionRoomtimeFormatter(timeZone), dbHelper)
                )
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

