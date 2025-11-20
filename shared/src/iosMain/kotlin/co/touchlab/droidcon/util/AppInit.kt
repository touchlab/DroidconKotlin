package co.touchlab.droidcon.util

// TODO: Add crashkios dependency if Crashlytics is needed for iOS
// import co.touchlab.crashkios.crashlytics.enableCrashlytics
// import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook
import co.touchlab.kermit.ExperimentalKermitApi
// import co.touchlab.kermit.Severity
// import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter

@OptIn(ExperimentalKermitApi::class)
fun setupKermit() {
    // TODO: Re-enable when crashkios dependency is added
    // Logger.addLogWriter(CrashlyticsLogWriter(minSeverity = Severity.Info, minCrashSeverity = Severity.Warn))
    // enableCrashlytics()
    // setCrashlyticsUnhandledExceptionHook()
}
