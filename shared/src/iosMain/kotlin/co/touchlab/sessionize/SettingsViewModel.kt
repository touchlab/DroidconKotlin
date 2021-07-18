package co.touchlab.sessionize

import co.touchlab.sessionize.platform.NotificationsModel

class SettingsViewModel() {
    val settingsModel = SettingsModel(ServiceRegistry.notificationsApi, ServiceRegistry.coroutinesDispatcher)
}