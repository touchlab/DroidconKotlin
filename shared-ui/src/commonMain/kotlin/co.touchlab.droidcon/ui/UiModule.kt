package co.touchlab.droidcon.ui

import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.viewmodel.ApplicationComponent
import co.touchlab.droidcon.viewmodel.FeedbackDialogComponent
import co.touchlab.droidcon.viewmodel.TabComponent
import co.touchlab.droidcon.viewmodel.session.AgendaComponent
import co.touchlab.droidcon.viewmodel.session.ScheduleComponent
import co.touchlab.droidcon.viewmodel.session.SessionDayComponent
import co.touchlab.droidcon.viewmodel.session.SessionDaysComponent
import co.touchlab.droidcon.viewmodel.session.SessionDetailComponent
import co.touchlab.droidcon.viewmodel.session.SpeakerDetailComponent
import co.touchlab.droidcon.viewmodel.settings.AboutComponent
import co.touchlab.droidcon.viewmodel.settings.SettingsComponent
import co.touchlab.droidcon.viewmodel.sponsor.SponsorDetailComponent
import co.touchlab.droidcon.viewmodel.sponsor.SponsorListComponent
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
    // MARK: View model factories.
    single {
        ApplicationComponent(
            componentContext = get(),
            dispatchers = get(),
            tabFactory = get(),
            feedbackDialogFactory = get(),
            syncService = get(),
            notificationSchedulingService = get(),
            feedbackService = get(),
            settingsGateway = get(),
            urlHandler = get(),
        )
            .also { get<NotificationService>().setHandler(it) }
    }

    single {
        TabComponent.Factory(
            scheduleFactory = get(),
            agendaFactory = get(),
            sponsorsFactory = get(),
            settingsFactory = get(),
            sessionDetailFactory = get(),
            sponsorDetailFactory = get(),
            speakerDetailFactory = get(),
        )
    }

    single {
        ScheduleComponent.Factory(
            dispatchers = get(),
            sessionGateway = get(),
            sessionDaysFactory = get(),
            dateTimeService = get(),
        )
    }
    single {
        AgendaComponent.Factory(
            dispatchers = get(),
            sessionGateway = get(),
            sessionDaysFactory = get(),
            dateTimeService = get(),
        )
    }
    single { SessionDayComponent.Factory(dispatchers = get(), dateFormatter = get(), dateTimeService = get()) }
    single { SessionDaysComponent.Factory(sessionDayFactory = get(), dateFormatter = get()) }

    single {
        SessionDetailComponent.Factory(
            dispatchers = get(),
            sessionGateway = get(),
            dateFormatter = get(),
            dateTimeService = get(),
            parseUrlViewService = get(),
            settingsGateway = get(),
        )
    }
    single { SpeakerDetailComponent.Factory(parseUrlViewService = get()) }

    single { SponsorListComponent.Factory(dispatchers = get(), sponsorGateway = get()) }
    single { SponsorDetailComponent.Factory(dispatchers = get(), sponsorGateway = get()) }

    single { SettingsComponent.Factory(dispatchers = get(), settingsGateway = get(), aboutFactory = get()) }
    single { AboutComponent.Factory(dispatchers = get(), aboutRepository = get(), parseUrlViewService = get()) }

    single { FeedbackDialogComponent.Factory(dispatchers = get(), log = get(parameters = { parametersOf("FeedbackDialogComponent") })) }
}
