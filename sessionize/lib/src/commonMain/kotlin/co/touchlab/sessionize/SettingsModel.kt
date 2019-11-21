package co.touchlab.sessionize

import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.platform.NotificationsModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class SettingsModel(
        private val notificationsApi: NotificationsApi,
        coroutineDispatcher: CoroutineDispatcher) : BaseModel(coroutineDispatcher) {

    fun setRemindersSettingEnabled(enabled:Boolean) = mainScope.launch {
        NotificationsModel.remindersEnabled = enabled

        if (enabled && !NotificationsModel.notificationsEnabled) {
            notificationsApi.initializeNotifications {
                mainScope.launch {
                    NotificationsModel.recreateReminderNotifications()
                }
            }
        }else{
            NotificationsModel.recreateReminderNotifications()
        }
    }

    fun setFeedbackSettingEnabled(enabled:Boolean) = mainScope.launch {
        NotificationsModel.feedbackEnabled = enabled

        if (enabled && !NotificationsModel.notificationsEnabled) {
            notificationsApi.initializeNotifications {
                mainScope.launch {
                    NotificationsModel.recreateFeedbackNotifications()
                }
            }
        }else{
            NotificationsModel.recreateFeedbackNotifications()
        }
    }
}