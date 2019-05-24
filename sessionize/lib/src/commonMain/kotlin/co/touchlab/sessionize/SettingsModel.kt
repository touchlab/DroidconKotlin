package co.touchlab.sessionize

import co.touchlab.sessionize.db.SessionizeDbHelper.sessionQueries
import co.touchlab.sessionize.platform.NotificationsModel
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
                NotificationsModel.recreateReminderNotifications()
            }
        }else{
            NotificationsModel.recreateReminderNotifications()
        }
    }

    fun setFeedbackSettingEnabled(enabled:Boolean) = launch {
        setFeedbackEnabled(enabled)

        if(enabled && !notificationsEnabled()){
            ServiceRegistry.notificationsApi.initializeNotifications{success ->
                NotificationsModel.recreateFeedbackNotifications()
            }
        }else{
            NotificationsModel.recreateFeedbackNotifications()
        }
    }
}