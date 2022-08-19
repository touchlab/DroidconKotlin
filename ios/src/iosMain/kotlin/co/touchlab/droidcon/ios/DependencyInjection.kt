package co.touchlab.droidcon.ios

import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.ios.service.DefaultParseUrlViewService
import co.touchlab.droidcon.ios.util.DefaultUrlHandler
import co.touchlab.droidcon.ios.util.NotificationLocalizedStringFactory
import co.touchlab.droidcon.ios.util.formatter.IOSDateFormatter
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.ui.uiModule
import co.touchlab.droidcon.util.BundleResourceReader
import co.touchlab.droidcon.util.DcDispatchers
import co.touchlab.droidcon.util.UrlHandler
import co.touchlab.droidcon.util.formatter.DateFormatter
import co.touchlab.droidcon.viewmodel.ApplicationComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

@OptIn(ExperimentalSettingsApi::class)
fun initKoinIos(
    userDefaults: NSUserDefaults,
    analyticsService: AnalyticsService,
): KoinApplication = initKoin(
    module {
        single { DcDispatchers() }
        single { LifecycleRegistry() }
        single<UrlHandler> { DefaultUrlHandler() }
        single<ComponentContext> { DefaultComponentContext(lifecycle = get<LifecycleRegistry>()) }

        single { BundleProvider(bundle = NSBundle.mainBundle) }
        single<ObservableSettings> { AppleSettings(delegate = userDefaults) }
        single<ResourceReader> { BundleResourceReader(bundle = get<BundleProvider>().bundle) }

        single<DateFormatter> { IOSDateFormatter(dateTimeService = get()) }

        single<NotificationSchedulingService.LocalizedStringFactory> {
            NotificationLocalizedStringFactory(bundle = get<BundleProvider>().bundle)
        }

        single { analyticsService }

        single<ParseUrlViewService> { DefaultParseUrlViewService() }

    } + uiModule
)

// Workaround class for injecting an `NSObject` class.
// When not used, an error "KClass of Objective-C classes is not supported." is thrown.
data class BundleProvider(val bundle: NSBundle)

val Koin.applicationComponent: ApplicationComponent
    get() = get()

val Koin.applicationComponentLifecycle: LifecycleRegistry
    get() = get()
