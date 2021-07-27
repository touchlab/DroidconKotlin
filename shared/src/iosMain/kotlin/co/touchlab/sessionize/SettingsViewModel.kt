package co.touchlab.sessionize

import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SettingsViewModel():KoinComponent {
    val settingsModel = SettingsModel(get(), get())
}