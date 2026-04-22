package co.touchlab.droidcon.web

import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.impl.ComposeResourceReader
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.service.JsNotificationService
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.ui.uiModule
import co.touchlab.droidcon.util.formatter.DateFormatter
import co.touchlab.droidcon.util.formatter.KotlinXDateFormatter
import co.touchlab.droidcon.web.service.DefaultParseUrlViewService
import co.touchlab.droidcon.web.util.NotificationLocalizedStringFactory
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.observable.makeObservable
import kotlin.test.Test
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

class WebKoinTest : KoinTest {

    @OptIn(ExperimentalSettingsApi::class)
    @Test
    fun `should inject my components`() {
        val mainModule = module {
            single<ObservableSettings> {
                val storageSettings: Settings = StorageSettings()
                storageSettings.makeObservable()
            }
            single<ResourceReader> { ComposeResourceReader() }

            single<DateFormatter> { KotlinXDateFormatter() }

            single<NotificationSchedulingService.LocalizedStringFactory> { NotificationLocalizedStringFactory() }

            single<AnalyticsService> { WebAnalyticsService() }

            single<ParseUrlViewService> { DefaultParseUrlViewService() }

            // Provide JsNotificationService which is required by the JS platform module
            single<JsNotificationService> { JsNotificationService() }
        }

        koinApplication {
            modules(mainModule, uiModule)
            checkModules()
        }
    }
}
