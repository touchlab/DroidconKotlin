package co.touchlab.sessionize

import co.touchlab.sessionize.AppContext.FEEDBACK_ENABLED
import co.touchlab.sessionize.AppContext.REMINDERS_ENABLED
import co.touchlab.sessionize.AppContext.sessionQueries
import co.touchlab.sessionize.platform.backgroundTask
import co.touchlab.sessionize.platform.cancelFeedbackNotificationsForSessions
import co.touchlab.sessionize.platform.cancelReminderNotificationsForSessions
import co.touchlab.sessionize.platform.createFeedbackNotificationsForSessions
import co.touchlab.sessionize.platform.createReminderNotificationsForSessions
import co.touchlab.sessionize.platform.feedbackEnabled
import co.touchlab.sessionize.platform.notificationsEnabled
import co.touchlab.sessionize.platform.reminderNotificationsEnabled
import co.touchlab.sessionize.platform.setFeedbackEnabled
import co.touchlab.sessionize.platform.setRemindersEnabled
import com.russhwolf.settings.set
import kotlinx.coroutines.launch


class SettingsModel : BaseModel(ServiceRegistry.coroutinesDispatcher) {

    fun setRemindersSettingEnabled(enabled:Boolean) = launch {
        setRemindersEnabled(enabled)

        if(enabled && !notificationsEnabled()){
            ServiceRegistry.notificationsApi.initializeNotifications{success ->
                if(success) {
                    handleReminderNotifications(enabled)
                    handleFeedbackNotifications(feedbackEnabled())
                }
            }
        }else{
            handleReminderNotifications(enabled)
        }
    }

    fun setFeedbackSettingEnabled(enabled:Boolean) = launch {
        setFeedbackEnabled(enabled)

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
                createReminderNotificationsForSessions(mySessions)
            }else{
                cancelReminderNotificationsForSessions(mySessions)
            }
        }
    }

    private fun handleFeedbackNotifications(create:Boolean){
        backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
            if(create){
                createFeedbackNotificationsForSessions(mySessions)
            }else {
                cancelFeedbackNotificationsForSessions(mySessions)
            }
        }
    }
}