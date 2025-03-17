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
