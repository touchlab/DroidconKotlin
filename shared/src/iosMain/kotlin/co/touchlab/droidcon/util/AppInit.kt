package co.touchlab.droidcon.util

import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter

@OptIn(ExperimentalKermitApi::class)
fun setupKermit() {
    Logger.addLogWriter(
        CrashlyticsLogWriter(
            minSeverity = Severity.Info,
            minCrashSeverity = Severity.Warn
        )
    )
    enableCrashlytics()
    setCrashlyticsUnhandledExceptionHook()
}
