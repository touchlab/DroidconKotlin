package co.touchlab.droidcon

import app.cash.sqldelight.db.SqlDriver
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.repository.impl.SqlDelightDriverFactory
import co.touchlab.droidcon.service.AndroidNotificationService
import co.touchlab.droidcon.util.formatter.AndroidDateFormatter
import co.touchlab.droidcon.util.formatter.DateFormatter
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

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
            json = get(),
        )
    }

    single<DateFormatter> {
        AndroidDateFormatter(dateTimeService = get())
    }

    val baseKermit = Logger(config = StaticConfig(logWriterList = listOf(LogcatWriter())), tag = "Droidcon")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
}

actual val sentryDsn = "https://6a97c8a17bd4ce7adc4a93d0bd3cb300@o4506955696766976.ingest.us.sentry.io/4506956255854592"
