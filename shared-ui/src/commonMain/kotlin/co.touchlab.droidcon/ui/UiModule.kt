package co.touchlab.droidcon.ui

import co.touchlab.droidcon.viewmodel.ViewModelFactory
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel
import co.touchlab.droidcon.viewmodel.session.SessionDetailScrollStateStorage
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
    // MARK: View model factories.
    single {
        ViewModelFactory.ApplicationViewModelFactory(
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
        ViewModelFactory.ScheduleViewModelFactory(
            sessionGateway = get(),
            sessionDayFactory = get(),
            sessionDetailFactory = get(),
            sessionDetailScrollStateStorage = get(),
            dateTimeService = get(),
            conferenceConfigProvider = get(),
        )
    }
    single {
        ViewModelFactory.AgendaViewModelFactory(
            sessionGateway = get(),
            sessionDayFactory = get(),
            sessionDetailFactory = get(),
            sessionDetailScrollStateStorage = get(),
            dateTimeService = get(),
            conferenceConfigProvider = get(),
        )
    }
    single { ViewModelFactory.SessionBlockViewModelFactory(sessionListItemFactory = get(), dateFormatter = get()) }
    single {
        ViewModelFactory.SessionDayViewModelFactory(
            sessionBlockFactory = get(),
            dateFormatter = get(),
            dateTimeService = get(),
            conferenceConfigProvider = get(),
            sessionDetailScrollStateStorage = get(),
        )
    }
    single { ViewModelFactory.SessionListItemViewModelFactory(dateTimeService = get()) }

    single {
        ViewModelFactory.SessionDetailViewModelFactory(
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
    single { ViewModelFactory.SpeakerListItemViewModelFactory() }

    single { ViewModelFactory.SpeakerDetailViewModelFactory(parseUrlViewService = get()) }

    single { ViewModelFactory.SponsorListViewModelFactory(sponsorGateway = get(), sponsorGroupFactory = get(), sponsorDetailFactory = get()) }
    single { ViewModelFactory.SponsorGroupViewModelFactory(sponsorGroupItemFactory = get()) }
    single { ViewModelFactory.SponsorGroupItemViewModelFactory() }
    single { ViewModelFactory.SponsorDetailViewModelFactory(sponsorGateway = get(), speakerListItemFactory = get(), speakerDetailFactory = get()) }

    single { ViewModelFactory.SettingsViewModelFactory(settingsGateway = get(), aboutFactory = get(), conferenceRepository = get()) }
    single { ViewModelFactory.AboutViewModelFactory(aboutRepository = get(), parseUrlViewService = get()) }

    single { ViewModelFactory.FeedbackDialogViewModelFactory(sessionGateway = get(), get(parameters = { parametersOf("FeedbackDialogViewModel") })) }

    single { SessionDetailScrollStateStorage() }
}
