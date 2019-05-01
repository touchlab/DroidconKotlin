package co.touchlab.sessionize

import co.touchlab.sessionize.AppContext.FEEDBACK_ENABLED
import co.touchlab.sessionize.AppContext.REMINDERS_ENABLED
import co.touchlab.sessionize.AppContext.sessionQueries
import co.touchlab.sessionize.platform.backgroundTask
import co.touchlab.sessionize.platform.feedbackEnabled
import co.touchlab.sessionize.platform.notificationsEnabled
import co.touchlab.sessionize.platform.reminderNotificationsEnabled
import com.russhwolf.settings.set
import kotlinx.coroutines.launch


class SettingsModel : BaseModel(ServiceRegistry.coroutinesDispatcher) {

    fun setRemindersEnabled(enabled:Boolean) = launch {
        ServiceRegistry.appSettings[REMINDERS_ENABLED] = enabled

        if(enabled && !notificationsEnabled()){
            ServiceRegistry.notificationsApi.initializeNotifications{success ->
                if(success) {
                    handleReminderNotifications(enabled)
                    handleFeedbackNotifications(feedbackEnabled())
                }
            }
        }else{
            handleFeedbackNotifications(enabled)
        }
    }

    fun setFeedbackEnabled(enabled:Boolean) = launch {
        ServiceRegistry.appSettings[FEEDBACK_ENABLED] = enabled

        if(enabled && !notificationsEnabled()){
            ServiceRegistry.notificationsApi.initializeNotifications{success ->
                if(success) {
                    handleFeedbackNotifications(enabled)
                    handleReminderNotifications(reminderNotificationsEnabled())
                }
            }
        }else{
            handleFeedbackNotifications(enabled)
        }
    }

    private fun handleReminderNotifications(create:Boolean){
        backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
            if(create){
                ServiceRegistry.notificationsApi.createReminderNotificationsForSessions(mySessions)
            }else{
                ServiceRegistry.notificationsApi.cancelReminderNotificationsForSessions(mySessions)
            }
        }
    }

    private fun handleFeedbackNotifications(create:Boolean){
        backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
            if(create){
                ServiceRegistry.notificationsApi.createFeedbackNotificationsForSessions(mySessions)
            }else {
                ServiceRegistry.notificationsApi.cancelFeedbackNotificationsForSessions(mySessions)
            }
        }
    }
}