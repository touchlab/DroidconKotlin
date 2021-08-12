package co.touchlab.droidcon.ios

import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.ios.util.NotificationLocalizedStringFactory
import co.touchlab.droidcon.ios.util.formatter.DateFormatter
import co.touchlab.droidcon.ios.viewmodel.settings.AboutViewModel
import co.touchlab.droidcon.ios.viewmodel.session.AgendaViewModel
import co.touchlab.droidcon.ios.viewmodel.ApplicationViewModel
import co.touchlab.droidcon.ios.viewmodel.session.ScheduleViewModel
import co.touchlab.droidcon.ios.viewmodel.session.SessionBlockViewModel
import co.touchlab.droidcon.ios.viewmodel.session.SessionDayViewModel
import co.touchlab.droidcon.ios.viewmodel.session.SessionDetailViewModel
import co.touchlab.droidcon.ios.viewmodel.session.SessionListItemViewModel
import co.touchlab.droidcon.ios.viewmodel.settings.SettingsViewModel
import co.touchlab.droidcon.ios.viewmodel.session.SpeakerDetailViewModel
import co.touchlab.droidcon.ios.viewmodel.session.SpeakerListItemViewModel
import co.touchlab.droidcon.ios.viewmodel.sponsor.SponsorDetailViewModel
import co.touchlab.droidcon.ios.viewmodel.sponsor.SponsorGroupItemViewModel
import co.touchlab.droidcon.ios.viewmodel.sponsor.SponsorGroupViewModel
import co.touchlab.droidcon.ios.viewmodel.sponsor.SponsorListViewModel
import co.touchlab.droidcon.util.BundleResourceReader
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
        factory { ApplicationViewModel(get(), get(), get(), get(), get(), get()) }

        factory { ScheduleViewModel.Factory(get(), get(), get(), get()) }
        factory { AgendaViewModel.Factory(get(), get(), get(), get()) }
        factory { SessionBlockViewModel.Factory(get(), get()) }
        factory { SessionDayViewModel.Factory(get(), get(), get()) }
        factory { SessionListItemViewModel.Factory(get()) }

        factory { SessionDetailViewModel.Factory(get(), get(), get(), get(), get()) }
        factory { SpeakerListItemViewModel.Factory() }

        factory { SpeakerDetailViewModel.Factory() }

        factory { SponsorListViewModel.Factory(get(), get(), get()) }
        factory { SponsorGroupViewModel.Factory(get()) }
        factory { SponsorGroupItemViewModel.Factory() }
        factory { SponsorDetailViewModel.Factory(get(), get()) }

        factory { SettingsViewModel.Factory(get(), get()) }
        factory { AboutViewModel.Factory(get()) }
    }
)

// Workaround class for injecting an `NSObject` class.
// When not used, an error "KClass of Objective-C classes is not supported." is thrown.
data class BundleProvider(val bundle: NSBundle)

val Koin.applicationViewModel: ApplicationViewModel
    get() = get()