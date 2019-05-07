package co.touchlab.sessionize.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.touchlab.sessionize.SettingsModel

class SettingsViewModel: ViewModel(){
    val settingsModel = SettingsModel()
}

class SettingsViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel() as T
    }
}