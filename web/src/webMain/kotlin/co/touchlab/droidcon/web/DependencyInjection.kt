package co.touchlab.droidcon.web

import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.service.JsNotificationService
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.ui.uiModule
import co.touchlab.droidcon.util.formatter.DateFormatter
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel
import co.touchlab.droidcon.web.service.DefaultParseUrlViewService
import co.touchlab.droidcon.web.util.NotificationLocalizedStringFactory
import co.touchlab.droidcon.web.util.WebResourceReader
import co.touchlab.droidcon.web.util.formatter.WebDateFormatter
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.koin.core.KoinApplication
import org.koin.dsl.module
import com.russhwolf.settings.observable.makeObservable

@OptIn(ExperimentalSettingsApi::class)
fun startKoin(): KoinApplication =
    initKoin(
            module {
                single<ObservableSettings> {
                    val storageSettings: Settings = StorageSettings()
                    storageSettings.makeObservable()
                }
                single<ResourceReader> { WebResourceReader() }

                single<DateFormatter> { WebDateFormatter() }

                single<NotificationSchedulingService.LocalizedStringFactory> { NotificationLocalizedStringFactory() }

                single<AnalyticsService> { WebAnalyticsService() }

                single<ParseUrlViewService> { DefaultParseUrlViewService() }

                // Provide JsNotificationService which is required by the JS platform module
                single<JsNotificationService> { JsNotificationService() }
            } + uiModule
    )

@OptIn(ExperimentalWasmJsInterop::class)
@Suppress("unused")
val KoinApplication.waitForLoadedContextModel: WaitForLoadedContextModel
    get() = get()
