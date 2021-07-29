package co.touchlab.droidcon.application.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val isFeedbackEnabled: StateFlow<Boolean>

    val isRemindersEnabled: StateFlow<Boolean>

}