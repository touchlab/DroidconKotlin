package co.touchlab.droidcon.application.gateway

import co.touchlab.droidcon.application.composite.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsGateway {

    fun settings(): Settings

    fun observeSettings(): Flow<Settings>

    suspend fun setFeedbackEnabled(enabled: Boolean)

    suspend fun setSettingsEnabled(enabled: Boolean)

}