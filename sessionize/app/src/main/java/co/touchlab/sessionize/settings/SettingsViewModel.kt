package co.touchlab.sessionize.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsModel
import co.touchlab.sessionize.platform.NotificationsModel

class SettingsViewModel: ViewModel(){
    val settingsModel = SettingsModel(NotificationsModel, ServiceRegistry.notificationsApi, ServiceRegistry.coroutinesDispatcher)
}

class SettingsViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel() as T
    }
}