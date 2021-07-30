package co.touchlab.droidcon.android.viewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.application.gateway.SettingsGateway
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel: ViewModel(), KoinComponent {

    val isRemindersEnabled = MutableStateFlow(false)
    val isFeedbackEnabled = MutableStateFlow(false)

    private val settingsGateway by inject<SettingsGateway>()

    init {
        viewModelScope.launch {
            settingsGateway.observeSettings()
                .collect { settings ->
                    isRemindersEnabled.value = settings.isRemindersEnabled
                    isFeedbackEnabled.value = settings.isFeedbackEnabled
                }
        }

        viewModelScope.launch {
            isRemindersEnabled.collect(settingsGateway::setRemindersEnabled)
        }

        viewModelScope.launch {
            isFeedbackEnabled.collect(settingsGateway::setFeedbackEnabled)
        }
    }
}