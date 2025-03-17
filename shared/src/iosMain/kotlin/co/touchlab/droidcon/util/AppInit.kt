package co.touchlab.droidcon.util

import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalKermitApi::class)
fun setupKermit() {
    Logger.addLogWriter(CrashlyticsLogWriter(minSeverity = Severity.Info, minCrashSeverity = Severity.Warn))
    enableCrashlytics()
    setCrashlyticsUnhandledExceptionHook()
}

/**
 * Initialize the conferences in the database
 */
fun initializeConferences() {
    val initializer = ConferenceInitializer()
    initializer.initConferences()
}

/**
 * Helper class to initialize conferences from iOS
 */
private class ConferenceInitializer : KoinComponent {
    private val conferenceRepository: ConferenceRepository by inject()
    private val log: Logger by inject(qualifier = org.koin.core.qualifier.named("ConferenceInitializer"))
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun initConferences() {
        coroutineScope.launch {
            try {
                conferenceRepository.initConferencesIfNeeded()
            } catch (e: Exception) {
                log.e(e) { "Error initializing conferences" }
            }
        }
    }
}
