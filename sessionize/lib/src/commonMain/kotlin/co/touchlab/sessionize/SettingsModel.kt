package co.touchlab.sessionize

import co.touchlab.sessionize.AppContext.FEEDBACK_ENABLED
import co.touchlab.sessionize.AppContext.REMINDERS_ENABLED
import co.touchlab.sessionize.platform.initializeNotifications
import com.russhwolf.settings.set
import kotlinx.coroutines.launch


class SettingsModel : BaseModel(ServiceRegistry.coroutinesDispatcher) {

    fun setFeedbackEnabled(enabled:Boolean) = launch {
        if(enabled){
            initializeNotifications()
        }

        ServiceRegistry.appSettings[FEEDBACK_ENABLED] = enabled
    }

    fun setRemindersEnabled(enabled:Boolean) = launch {
        if(enabled){
            initializeNotifications()
        }

        ServiceRegistry.appSettings[REMINDERS_ENABLED] = enabled
    }
}