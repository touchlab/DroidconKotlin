package co.touchlab.droidcon.application.repository.impl

import co.touchlab.droidcon.application.composite.Settings
import co.touchlab.droidcon.application.repository.SettingsRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalSettingsApi::class)
class DefaultSettingsRepository(private val observableSettings: ObservableSettings) : SettingsRepository {
    private companion object {
        private const val SETTINGS_FEEDBACK_ENABLED_KEY = "SETTINGS_FEEDBACK_ENABLED"
        private const val SETTINGS_REMINDERS_ENABLED_KEY = "SETTINGS_REMINDERS_ENABLED"
        private const val SETTINGS_FIRST_RUN_KEY = "SETTINGS_FIRST_RUN"
    }

    private var isFeedbackEnabled: Boolean
        get() = observableSettings[SETTINGS_FEEDBACK_ENABLED_KEY, true]
        set(value) {
            observableSettings[SETTINGS_FEEDBACK_ENABLED_KEY] = value
        }

    private var isRemindersEnabled: Boolean
        get() = observableSettings[SETTINGS_REMINDERS_ENABLED_KEY, true]
        set(value) {
            observableSettings[SETTINGS_REMINDERS_ENABLED_KEY] = value
        }

    private var isFirstRun: Boolean
        get() = observableSettings[SETTINGS_FIRST_RUN_KEY, true]
        set(value) {
            observableSettings[SETTINGS_FIRST_RUN_KEY] = value
        }

    override val settings: MutableStateFlow<Settings> = MutableStateFlow(
        Settings(
            isFeedbackEnabled = isFeedbackEnabled,
            isRemindersEnabled = isRemindersEnabled,
            isFirstRun = isFirstRun,
        ),
    )

    override suspend fun set(settings: Settings) {
        isFeedbackEnabled = settings.isFeedbackEnabled
        isRemindersEnabled = settings.isRemindersEnabled
        isFirstRun = settings.isFirstRun
        this.settings.value = settings
    }

    override suspend fun setFeedbackEnabled(enabled: Boolean) {
        isFeedbackEnabled = enabled
        this.settings.value = this.settings.value.copy(
            isFeedbackEnabled = enabled,
        )
    }

    override suspend fun setRemindersEnabled(enabled: Boolean) {
        isRemindersEnabled = enabled
        this.settings.value = this.settings.value.copy(
            isRemindersEnabled = enabled,
        )
    }

    override suspend fun setFirstRun(isFirstRun: Boolean) {
        this.isFirstRun = isFirstRun
        this.settings.value = this.settings.value.copy(
            isFirstRun = isFirstRun,
        )
    }
}
