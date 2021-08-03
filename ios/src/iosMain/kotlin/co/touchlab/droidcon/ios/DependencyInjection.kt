package co.touchlab.droidcon.ios

import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.ios.util.NotificationLocalizedStringFactory
import co.touchlab.droidcon.ios.util.formatter.DateFormatter
import co.touchlab.droidcon.ios.viewmodel.AboutViewModel
import co.touchlab.droidcon.ios.viewmodel.AgendaViewModel
import co.touchlab.droidcon.ios.viewmodel.ApplicationViewModel
import co.touchlab.droidcon.ios.viewmodel.ScheduleViewModel
import co.touchlab.droidcon.ios.viewmodel.SessionBlockViewModel
import co.touchlab.droidcon.ios.viewmodel.SessionDayViewModel
import co.touchlab.droidcon.ios.viewmodel.SessionDetailViewModel
import co.touchlab.droidcon.ios.viewmodel.SessionListItemViewModel
import co.touchlab.droidcon.ios.viewmodel.SettingsViewModel
import co.touchlab.droidcon.ios.viewmodel.SpeakerDetailViewModel
import co.touchlab.droidcon.ios.viewmodel.SpeakerListItemViewModel
import co.touchlab.droidcon.util.BundleResourceReader
import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults
import kotlin.time.ExperimentalTime

@OptIn(
    ExperimentalTime::class,
    ExperimentalSettingsApi::class,
)
fun initKoinIos(
    userDefaults: NSUserDefaults,
): KoinApplication = initKoin(
    module {
        single { BundleProvider(NSBundle.mainBundle) }
        single<ObservableSettings> { AppleSettings(userDefaults) }
        single<ResourceReader> { BundleResourceReader(get<BundleProvider>().bundle) }

        single { DateFormatter(get()) }

        single<NotificationSchedulingService.LocalizedStringFactory> {
            NotificationLocalizedStringFactory(get<BundleProvider>().bundle)
        }

        // MARK: View model factories.
        factory { ApplicationViewModel(get(), get(), get(), get(), get()) }

        factory { ScheduleViewModel.Factory(get(), get(), get(), get()) }
        factory { AgendaViewModel.Factory(get(), get(), get(), get()) }
        factory { SessionBlockViewModel.Factory(get(), get()) }
        factory { SessionDayViewModel.Factory(get(), get(), get()) }
        factory { SessionListItemViewModel.Factory(get(), get()) }

        factory { SessionDetailViewModel.Factory(get(), get(), get(), get(), get()) }
        factory { SpeakerListItemViewModel.Factory() }

        factory { SpeakerDetailViewModel.Factory() }

        factory { SettingsViewModel.Factory(get(), get()) }
        factory { AboutViewModel.Factory() }
    }
)

// Workaround class for injecting an `NSObject` class.
// When not used, an error "KClass of Objective-C classes is not supported." is thrown.
data class BundleProvider(val bundle: NSBundle)

val Koin.applicationViewModel: ApplicationViewModel
    get() = get()