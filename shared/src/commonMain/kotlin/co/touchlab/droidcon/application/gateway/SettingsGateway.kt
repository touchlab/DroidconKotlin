package co.touchlab.droidcon.application.gateway

import co.touchlab.droidcon.application.composite.Settings
import kotlinx.coroutines.flow.StateFlow

interface SettingsGateway {

    fun settings(): StateFlow<Settings>

    suspend fun setFeedbackEnabled(enabled: Boolean)

    suspend fun setSettingsEnabled(enabled: Boolean)

}