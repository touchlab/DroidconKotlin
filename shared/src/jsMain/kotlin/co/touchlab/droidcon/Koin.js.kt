package co.touchlab.droidcon

import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.service.JsNotificationService
import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> {
        Js.create()
    }

    single<NotificationService> {
        get<JsNotificationService>()
    }

    val baseKermit = Logger(config = StaticConfig(logWriterList = listOf(CommonWriter())), tag = "Droidcon")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
}
