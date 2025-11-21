package co.touchlab.droidcon

import app.cash.sqldelight.db.SqlDriver
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.repository.impl.SqlDelightDriverFactory
import co.touchlab.droidcon.service.JsNotificationService
import co.touchlab.droidcon.util.formatter.DateFormatter
import co.touchlab.droidcon.util.formatter.JsDateFormatter
import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

actual val platformModule: org.koin.core.module.Module = module {
    single<SqlDriver> {
        SqlDelightDriverFactory().createDriver()
    }

    single<HttpClientEngine> {
        CIO.create()
    }

    single<NotificationService> {
        get<JsNotificationService>()
    }

    single<DateFormatter> {
        JsDateFormatter()
    }

    val baseKermit = Logger(config = StaticConfig(logWriterList = listOf(CommonWriter())), tag = "Droidcon")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
}
