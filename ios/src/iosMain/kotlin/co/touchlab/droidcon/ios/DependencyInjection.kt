package co.touchlab.droidcon.ios

import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.ios.service.DefaultParseUrlViewService
import co.touchlab.droidcon.ios.util.NotificationLocalizedStringFactory
import co.touchlab.droidcon.ios.util.formatter.DateFormatter
import co.touchlab.droidcon.ios.viewmodel.settings.AboutViewModel
import co.touchlab.droidcon.ios.viewmodel.session.AgendaViewModel
import co.touchlab.droidcon.ios.viewmodel.ApplicationViewModel
import co.touchlab.droidcon.ios.viewmodel.FeedbackDialogViewModel
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
import co.touchlab.droidcon.service.IOSNotificationService
import co.touchlab.droidcon.service.NotificationHandler
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.util.BundleResourceReader
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

        single { DateFormatter(dateTimeService = get()) }

        single<NotificationSchedulingService.LocalizedStringFactory> {
            NotificationLocalizedStringFactory(bundle = get<BundleProvider>().bundle)
        }

        single { analyticsService }

        single<ParseUrlViewService> { DefaultParseUrlViewService() }

        // MARK: View model factories.
        single {
            ApplicationViewModel(
                scheduleFactory = get(),
                agendaFactory = get(),
                sponsorsFactory = get(),
                settingsFactory = get(),
                feedbackDialogFactory = get(),
                syncService = get(),
                notificationSchedulingService = get(),
                feedbackService = get(),
                settingsGateway = get(),
            )
                .also { (get<NotificationService>() as IOSNotificationService).setHandler(it) }
        }

        single {
            ScheduleViewModel.Factory(
                sessionGateway = get(),
                sessionDayFactory = get(),
                sessionDetailFactory = get(),
                dateTimeService = get(),
            )
        }
        single {
            AgendaViewModel.Factory(
                sessionGateway = get(),
                sessionDayFactory = get(),
                sessionDetailFactory = get(),
                dateTimeService = get(),
            )
        }
        single { SessionBlockViewModel.Factory(sessionListItemFactory = get(), dateFormatter = get()) }
        single { SessionDayViewModel.Factory(sessionBlockFactory = get(), dateFormatter = get(), dateTimeService = get()) }
        single { SessionListItemViewModel.Factory(dateTimeService = get()) }

        single {
            SessionDetailViewModel.Factory(
                sessionGateway = get(),
                speakerListItemFactory = get(),
                speakerDetailFactory = get(),
                dateFormatter = get(),
                dateTimeService = get(),
                parseUrlViewService = get(),
                settingsGateway = get(),
                feedbackDialogFactory = get(),
                feedbackService = get(),
            )
        }
        single { SpeakerListItemViewModel.Factory() }

        single { SpeakerDetailViewModel.Factory(parseUrlViewService = get()) }

        single { SponsorListViewModel.Factory(sponsorGateway = get(), sponsorGroupFactory = get(), sponsorDetailFactory = get()) }
        single { SponsorGroupViewModel.Factory(sponsorGroupItemFactory = get()) }
        single { SponsorGroupItemViewModel.Factory() }
        single { SponsorDetailViewModel.Factory(sponsorGateway = get(), speakerListItemFactory = get(), speakerDetailFactory = get()) }

        single { SettingsViewModel.Factory(settingsGateway = get(), aboutFactory = get()) }
        single { AboutViewModel.Factory(aboutRepository = get(), parseUrlViewService = get()) }

        single { FeedbackDialogViewModel.Factory(sessionGateway = get(), get(parameters = { parametersOf("FeedbackDialogViewModel") })) }
    }
)

// Workaround class for injecting an `NSObject` class.
// When not used, an error "KClass of Objective-C classes is not supported." is thrown.
data class BundleProvider(val bundle: NSBundle)

val Koin.applicationViewModel: ApplicationViewModel
    get() = get()
