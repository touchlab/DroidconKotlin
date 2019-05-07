package co.touchlab.sessionize

import co.touchlab.sessionize.db.SessionizeDbHelper.sessionQueries
import co.touchlab.sessionize.platform.NotificationsModel.feedbackEnabled
import co.touchlab.sessionize.platform.NotificationsModel.notificationsEnabled
import co.touchlab.sessionize.platform.NotificationsModel.reminderNotificationsEnabled
import co.touchlab.sessionize.platform.NotificationsModel.setFeedbackEnabled
import co.touchlab.sessionize.platform.NotificationsModel.setRemindersEnabled
import co.touchlab.sessionize.platform.backgroundTask
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