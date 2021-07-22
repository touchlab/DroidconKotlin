package co.touchlab.sessionize.event

import androidx.lifecycle.ViewModel
import co.touchlab.sessionize.EventModel
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.platform.NotificationsModel

class EventViewModel(sessionId:String, dbHelper: SessionizeDbHelper, sessionizeApi: SessionizeApi, notificationsModel: NotificationsModel): ViewModel(){
    val eventModel = EventModel(sessionId, dbHelper, sessionizeApi, notificationsModel)
}