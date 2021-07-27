package co.touchlab.sessionize

import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.platform.NotificationsModel
import kotlinx.coroutines.launch

class SettingsModel(
        private val notificationsApi: NotificationsApi,
        private val notificationsModel: NotificationsModel) : BaseModel() {

    fun getRemindersSettingEnabled():Boolean = notificationsModel.remindersEnabled
    fun getFeedbackSettingEnabled():Boolean = notificationsModel.feedbackEnabled

    fun setRemindersSettingEnabled(enabled:Boolean) = mainScope.launch {
        notificationsModel.remindersEnabled = enabled

        if (enabled && !notificationsModel.notificationsEnabled) {
            notificationsApi.initializeNotifications {
                mainScope.launch {
                    notificationsModel.recreateReminderNotifications()
                }
            }
        }else{
            notificationsModel.recreateReminderNotifications()
        }
    }

    fun setFeedbackSettingEnabled(enabled:Boolean) = mainScope.launch {
        notificationsModel.feedbackEnabled = enabled

        if (enabled && !notificationsModel.notificationsEnabled) {
            notificationsApi.initializeNotifications {
                mainScope.launch {
                    notificationsModel.recreateFeedbackNotifications()
                }
            }
        }else{
            notificationsModel.recreateFeedbackNotifications()
        }
    }
}