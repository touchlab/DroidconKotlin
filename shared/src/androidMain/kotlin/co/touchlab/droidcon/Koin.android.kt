package co.touchlab.droidcon

import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.repository.impl.SqlDelightDriverFactory
import co.touchlab.droidcon.service.AndroidNotificationService
import co.touchlab.kermit.Kermit
import co.touchlab.kermit.LogcatLogger
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> {
        SqlDelightDriverFactory(get()).createDriver()
    }

    single<Settings> {
        AndroidSettings(get())
    }

    single<HttpClientEngine> {
        OkHttp.create {}
    }

    single<NotificationService> {
        AndroidNotificationService(
            context = get(),
            entrypointActivity = get(),
            log = getWith("AndroidNotificationService"),
        )
    }

    val baseKermit = Kermit(LogcatLogger()).withTag("Droidcon")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
}

