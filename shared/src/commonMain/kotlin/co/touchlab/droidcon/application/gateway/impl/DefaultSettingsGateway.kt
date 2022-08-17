package co.touchlab.droidcon.application.gateway.impl

import co.touchlab.droidcon.application.composite.Settings
import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.application.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class DefaultSettingsGateway(
    private val settingsRepository: SettingsRepository,
): SettingsGateway {

    override fun settings(): StateFlow<Settings> = settingsRepository.settings

    override suspend fun setFeedbackEnabled(enabled: Boolean) {
        settingsRepository.setFeedbackEnabled(enabled)
    }

    override suspend fun setRemindersEnabled(enabled: Boolean) {
        settingsRepository.setRemindersEnabled(enabled)
    }

    override suspend fun setUseComposeForIos(useCompose: Boolean) {
        settingsRepository.setUseComposeForIos(useCompose)
    }
}
