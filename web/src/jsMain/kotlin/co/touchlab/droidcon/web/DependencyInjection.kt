package co.touchlab.droidcon.web

import app.cash.sqldelight.db.SqlDriver
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.db.DroidconDatabase
import co.touchlab.droidcon.domain.repository.impl.SqlDelightDriverFactory
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.impl.ComposeResourceReader
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.service.JsNotificationService
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.ui.uiModule
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel
import co.touchlab.droidcon.web.service.DefaultParseUrlViewService
import co.touchlab.droidcon.web.util.NotificationLocalizedStringFactory
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.observable.makeObservable
import kotlin.time.ExperimentalTime
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
fun webAppModule(driver: SqlDriver? = null): Module = module {
    single<ObservableSettings> {
        val storageSettings: Settings = StorageSettings()
        storageSettings.makeObservable()
    }
    single<ResourceReader> { ComposeResourceReader() }

    single<NotificationSchedulingService.LocalizedStringFactory> { NotificationLocalizedStringFactory() }

    single<AnalyticsService> { WebAnalyticsService() }

    single<ParseUrlViewService> { DefaultParseUrlViewService() }

    single<JsNotificationService> { JsNotificationService() }

    if (driver != null) {
        single<SqlDriver> { driver }
    }
}

@OptIn(ExperimentalSettingsApi::class, ExperimentalTime::class)
suspend fun startKoin(): KoinApplication {
    val driver = SqlDelightDriverFactory().createDriver()
    DroidconDatabase.Schema.create(driver).await()

    return initKoin(
        webAppModule(driver) + uiModule,
    )
}

@Suppress("unused")
val KoinApplication.waitForLoadedContextModel: WaitForLoadedContextModel
    get() = get()
