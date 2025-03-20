package co.touchlab.droidcon.ui

import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import co.touchlab.droidcon.viewmodel.FeedbackDialogViewModel
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel
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
        ApplicationViewModel.Factory(
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
            conferenceRepository = get(),
        )
    }

    single {
        WaitForLoadedContextModel(
            conferenceConfigProvider = get(),
            applicationViewModelFactory = get(),
            syncService = get(),
            settingsGateway = get(),
        )
    }

    single {
        ScheduleViewModel.Factory(
            sessionGateway = get(),
            sessionDayFactory = get(),
            sessionDetailFactory = get(),
            sessionDetailScrollStateStorage = get(),
            dateTimeService = get(),
            conferenceConfigProvider = get(),
        )
    }
    single {
        AgendaViewModel.Factory(
            sessionGateway = get(),
            sessionDayFactory = get(),
            sessionDetailFactory = get(),
            sessionDetailScrollStateStorage = get(),
            dateTimeService = get(),
            conferenceConfigProvider = get(),
        )
    }
    single { SessionBlockViewModel.Factory(sessionListItemFactory = get(), dateFormatter = get()) }
    single {
        SessionDayViewModel.Factory(
            sessionBlockFactory = get(),
            dateFormatter = get(),
            dateTimeService = get(),
            conferenceConfigProvider = get(),
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
            conferenceConfigProvider = get(),
            notificationService = get(),
        )
    }
    single { SpeakerListItemViewModel.Factory() }

    single { SpeakerDetailViewModel.Factory(parseUrlViewService = get()) }

    single { SponsorListViewModel.Factory(sponsorGateway = get(), sponsorGroupFactory = get(), sponsorDetailFactory = get()) }
    single { SponsorGroupViewModel.Factory(sponsorGroupItemFactory = get()) }
    single { SponsorGroupItemViewModel.Factory() }
    single { SponsorDetailViewModel.Factory(sponsorGateway = get(), speakerListItemFactory = get(), speakerDetailFactory = get()) }

    single { SettingsViewModel.Factory(settingsGateway = get(), aboutFactory = get(), conferenceRepository = get()) }
    single { AboutViewModel.Factory(aboutRepository = get(), parseUrlViewService = get()) }

    single { FeedbackDialogViewModel.Factory(sessionGateway = get(), get(parameters = { parametersOf("FeedbackDialogViewModel") })) }

    single { SessionDetailScrollStateStorage() }
}
