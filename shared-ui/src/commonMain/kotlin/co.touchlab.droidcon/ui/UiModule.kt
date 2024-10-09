package co.touchlab.droidcon.ui

import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import co.touchlab.droidcon.viewmodel.FeedbackDialogViewModel
import co.touchlab.droidcon.viewmodel.session.AgendaViewModel
import co.touchlab.droidcon.viewmodel.session.ScheduleViewModel
import co.touchlab.droidcon.viewmodel.session.SessionBlockViewModel
import co.touchlab.droidcon.viewmodel.session.SessionDayViewModel
import co.touchlab.droidcon.viewmodel.session.SessionDetailScrollStateStorage
import co.touchlab.droidcon.viewmodel.session.SessionDetailViewModel
import co.touchlab.droidcon.viewmodel.session.SessionListItemViewModel
import co.touchlab.droidcon.viewmodel.session.SpeakerDetailViewModel
import co.touchlab.droidcon.viewmodel.session.SpeakerListItemViewModel
import co.touchlab.droidcon.viewmodel.settings.AboutViewModel
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorDetailViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorGroupItemViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorGroupViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorListViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
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
            notificationService = get(),
            feedbackService = get(),
            settingsGateway = get(),
        )
            .also { get<NotificationService>().setHandler(it) }
    }

    single {
        ScheduleViewModel.Factory(
            sessionGateway = get(),
            sessionDayFactory = get(),
            sessionDetailFactory = get(),
            sessionDetailScrollStateStorage = get(),
            dateTimeService = get(),
        )
    }
    single {
        AgendaViewModel.Factory(
            sessionGateway = get(),
            sessionDayFactory = get(),
            sessionDetailFactory = get(),
            sessionDetailScrollStateStorage = get(),
            dateTimeService = get(),
        )
    }
    single { SessionBlockViewModel.Factory(sessionListItemFactory = get(), dateFormatter = get()) }
    single {
        SessionDayViewModel.Factory(
            sessionBlockFactory = get(),
            dateFormatter = get(),
            dateTimeService = get(),
            sessionDetailScrollStateStorage = get(),
        )
    }
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
            notificationService = get(),
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

    single { SessionDetailScrollStateStorage() }
}
