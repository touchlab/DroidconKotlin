package co.touchlab.sessionize.settings

import androidx.lifecycle.ViewModel
import co.touchlab.sessionize.SettingsModel
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.platform.NotificationsModel

class SettingsViewModel(
    notificationsApi: NotificationsApi,
    notificationsModel: NotificationsModel
) : ViewModel() {
    val settingsModel = SettingsModel(notificationsApi, notificationsModel)
}