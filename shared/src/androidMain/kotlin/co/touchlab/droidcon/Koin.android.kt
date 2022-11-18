package co.touchlab.droidcon

import app.cash.sqldelight.db.SqlDriver
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.repository.impl.SqlDelightDriverFactory
import co.touchlab.droidcon.service.AndroidNotificationService
import co.touchlab.droidcon.util.formatter.AndroidDateFormatter
import co.touchlab.droidcon.util.formatter.DateFormatter
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class, ExperimentalKermitApi::class)
actual val platformModule: Module = module {
    single<SqlDriver> {
        SqlDelightDriverFactory(context = get()).createDriver()
    }

    single<HttpClientEngine> {
        OkHttp.create {}
    }

    single<NotificationService> {
        AndroidNotificationService(
            context = get(),
            entrypointActivity = get(),
            log = getWith("AndroidNotificationService"),
            settings = get(),
            json = get()
        )
    }

    single<DateFormatter> {
        AndroidDateFormatter(dateTimeService = get())
    }

    val baseKermit = Logger(config = StaticConfig(logWriterList = listOf(LogcatWriter(), CrashlyticsLogWriter())), tag = "Droidcon")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
}
