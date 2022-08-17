package co.touchlab.droidcon.ios

import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.ios.service.DefaultParseUrlViewService
import co.touchlab.droidcon.ios.util.NotificationLocalizedStringFactory
import co.touchlab.droidcon.ios.util.formatter.IOSDateFormatter
import co.touchlab.droidcon.viewmodel.settings.AboutViewModel
import co.touchlab.droidcon.viewmodel.session.AgendaViewModel
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import co.touchlab.droidcon.viewmodel.FeedbackDialogViewModel
import co.touchlab.droidcon.viewmodel.session.ScheduleViewModel
import co.touchlab.droidcon.viewmodel.session.SessionBlockViewModel
import co.touchlab.droidcon.viewmodel.session.SessionDayViewModel
import co.touchlab.droidcon.viewmodel.session.SessionDetailViewModel
import co.touchlab.droidcon.viewmodel.session.SessionListItemViewModel
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel
import co.touchlab.droidcon.viewmodel.session.SpeakerDetailViewModel
import co.touchlab.droidcon.viewmodel.session.SpeakerListItemViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorDetailViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorGroupItemViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorGroupViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorListViewModel
import co.touchlab.droidcon.service.IOSNotificationService
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.util.BundleResourceReader
import co.touchlab.droidcon.util.formatter.DateFormatter
import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

@OptIn(ExperimentalSettingsApi::class)
fun initKoinIos(
    userDefaults: NSUserDefaults,
    analyticsService: AnalyticsService,
): KoinApplication = initKoin(
    module {
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

val Koin.applicationViewModel: ApplicationViewModel
    get() = get()
