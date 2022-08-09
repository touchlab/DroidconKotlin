package co.touchlab.droidcon.util

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import co.touchlab.kermit.crashlytics.setupCrashlyticsExceptionHook

@OptIn(ExperimentalKermitApi::class)
fun setupKermit() {
    Logger.addLogWriter(CrashlyticsLogWriter(minSeverity = Severity.Info, minCrashSeverity = Severity.Warn, printTag = true))
    setupCrashlyticsExceptionHook(Logger)
}
