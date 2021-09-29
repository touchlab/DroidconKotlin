package co.touchlab.droidcon.application.repository

import co.touchlab.droidcon.application.composite.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val settings: StateFlow<Settings>

    suspend fun set(settings: Settings)

    suspend fun setFeedbackEnabled(enabled: Boolean)

    suspend fun setRemindersEnabled(enabled: Boolean)
}