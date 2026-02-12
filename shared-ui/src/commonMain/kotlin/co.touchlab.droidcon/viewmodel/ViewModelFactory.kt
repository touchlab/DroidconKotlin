package co.touchlab.droidcon.viewmodel

import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.application.repository.AboutRepository
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.FeedbackService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.service.ParseUrlViewService
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
import co.touchlab.kermit.Logger
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import co.touchlab.droidcon.util.formatter.DateFormatter
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
object ViewModelFactory {

    class AboutViewModelFactory(
        private val aboutRepository: AboutRepository,
        private val parseUrlViewService: ParseUrlViewService,
    ) {
        fun create() = AboutViewModel(aboutRepository, parseUrlViewService)
    }

    class SessionListItemViewModelFactory(private val dateTimeService: DateTimeService) {
        fun create(item: ScheduleItem, selected: () -> Unit) =
            SessionListItemViewModel(dateTimeService, item, selected)
    }

    class SpeakerListItemViewModelFactory {
        fun create(profile: Profile, selected: () -> Unit) =
            SpeakerListItemViewModel(profile, selected)
    }

    class SpeakerDetailViewModelFactory(private val parseUrlViewService: ParseUrlViewService) {
        fun create(profile: Profile) = SpeakerDetailViewModel(parseUrlViewService, profile)
    }

    class SessionBlockViewModelFactory(
        private val sessionListItemFactory: SessionListItemViewModelFactory,
        private val dateFormatter: DateFormatter,
    ) {
        fun create(startsAt: LocalDateTime, items: List<ScheduleItem>, onScheduleItemSelected: (ScheduleItem) -> Unit) =
            SessionBlockViewModel(sessionListItemFactory, dateFormatter, startsAt, items, onScheduleItemSelected)
    }

    class SessionDayViewModelFactory(
        private val sessionBlockFactory: SessionBlockViewModelFactory,
        private val dateFormatter: DateFormatter,
        private val dateTimeService: DateTimeService,
        private val conferenceConfigProvider: ConferenceConfigProvider,
        private val sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
    ) {
        fun create(date: LocalDate, attendingOnly: Boolean, items: List<ScheduleItem>, onScheduleItemSelected: (ScheduleItem) -> Unit) =
            SessionDayViewModel(
                sessionBlockFactory,
                dateFormatter,
                dateTimeService,
                conferenceConfigProvider,
                date,
                attendingOnly,
                sessionDetailScrollStateStorage,
                items,
                onScheduleItemSelected,
            )
    }

    class FeedbackDialogViewModelFactory(private val sessionGateway: SessionGateway, private val log: Logger) {
        fun create(
            session: Session,
            submit: suspend (Session.Feedback) -> Unit,
            closeAndDisable: (suspend () -> Unit)?,
            skip: suspend () -> Unit,
        ) = FeedbackDialogViewModel(sessionGateway, session, log, submit, closeAndDisable, skip)
    }

    class SessionDetailViewModelFactory(
        private val sessionGateway: SessionGateway,
        private val settingsGateway: SettingsGateway,
        private val conferenceConfigProvider: ConferenceConfigProvider,
        private val speakerListItemFactory: SpeakerListItemViewModelFactory,
        private val speakerDetailFactory: SpeakerDetailViewModelFactory,
        private val feedbackDialogFactory: FeedbackDialogViewModelFactory,
        private val dateFormatter: DateFormatter,
        private val dateTimeService: DateTimeService,
        private val parseUrlViewService: ParseUrlViewService,
        private val feedbackService: FeedbackService,
        private val notificationService: NotificationService,
    ) {
        fun create(item: ScheduleItem) = SessionDetailViewModel(
            sessionGateway = sessionGateway,
            settingsGateway = settingsGateway,
            conferenceConfigProvider = conferenceConfigProvider,
            speakerListItemFactory = speakerListItemFactory,
            speakerDetailFactory = speakerDetailFactory,
            feedbackDialogFactory = feedbackDialogFactory,
            dateFormatter = dateFormatter,
            dateTimeService = dateTimeService,
            parseUrlViewService = parseUrlViewService,
            feedbackService = feedbackService,
            notificationService = notificationService,
            initialItem = item,
        )
    }

    class ScheduleViewModelFactory(
        private val sessionGateway: SessionGateway,
        private val sessionDayFactory: SessionDayViewModelFactory,
        private val sessionDetailFactory: SessionDetailViewModelFactory,
        private val sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
        private val dateTimeService: DateTimeService,
        private val conferenceConfigProvider: ConferenceConfigProvider,
    ) {
        fun create() = ScheduleViewModel(
            sessionGateway,
            sessionDayFactory,
            sessionDetailFactory,
            sessionDetailScrollStateStorage,
            dateTimeService,
            conferenceConfigProvider,
        )
    }

    class AgendaViewModelFactory(
        private val sessionGateway: SessionGateway,
        private val sessionDayFactory: SessionDayViewModelFactory,
        private val sessionDetailFactory: SessionDetailViewModelFactory,
        private val sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
        private val dateTimeService: DateTimeService,
        private val conferenceConfigProvider: ConferenceConfigProvider,
    ) {
        fun create() = AgendaViewModel(
            sessionGateway,
            sessionDayFactory,
            sessionDetailFactory,
            sessionDetailScrollStateStorage,
            dateTimeService,
            conferenceConfigProvider,
        )
    }

    class SponsorGroupItemViewModelFactory {
        fun create(sponsor: Sponsor, selected: () -> Unit) =
            SponsorGroupItemViewModel(sponsor, selected)
    }

    class SponsorGroupViewModelFactory(private val sponsorGroupItemFactory: SponsorGroupItemViewModelFactory) {
        fun create(sponsorGroup: SponsorGroupWithSponsors, onSponsorSelected: (Sponsor) -> Unit) =
            SponsorGroupViewModel(sponsorGroupItemFactory, sponsorGroup, onSponsorSelected)
    }

    class SponsorDetailViewModelFactory(
        private val sponsorGateway: SponsorGateway,
        private val speakerListItemFactory: SpeakerListItemViewModelFactory,
        private val speakerDetailFactory: SpeakerDetailViewModelFactory,
    ) {
        fun create(sponsor: Sponsor, groupName: String) =
            SponsorDetailViewModel(sponsorGateway, speakerListItemFactory, speakerDetailFactory, sponsor, groupName)
    }

    class SponsorListViewModelFactory(
        private val sponsorGateway: SponsorGateway,
        private val sponsorGroupFactory: SponsorGroupViewModelFactory,
        private val sponsorDetailFactory: SponsorDetailViewModelFactory,
    ) {
        fun create() = SponsorListViewModel(sponsorGateway, sponsorGroupFactory, sponsorDetailFactory)
    }

    class SettingsViewModelFactory(
        private val settingsGateway: SettingsGateway,
        private val aboutFactory: AboutViewModelFactory,
        private val conferenceRepository: ConferenceRepository,
    ) {
        fun create() = SettingsViewModel(settingsGateway, aboutFactory, conferenceRepository)
    }

    class ApplicationViewModelFactory(
        private val scheduleFactory: ScheduleViewModelFactory,
        private val agendaFactory: AgendaViewModelFactory,
        private val sponsorsFactory: SponsorListViewModelFactory,
        private val settingsFactory: SettingsViewModelFactory,
        private val feedbackDialogFactory: FeedbackDialogViewModelFactory,
        private val syncService: SyncService,
        private val notificationSchedulingService: NotificationSchedulingService,
        private val notificationService: NotificationService,
        private val feedbackService: FeedbackService,
        private val settingsGateway: SettingsGateway,
        private val conferenceRepository: ConferenceRepository,
    ) {
        fun create(): ApplicationViewModel {
            val applicationViewModel = ApplicationViewModel(
                scheduleFactory = scheduleFactory,
                agendaFactory = agendaFactory,
                sponsorsFactory = sponsorsFactory,
                settingsFactory = settingsFactory,
                feedbackDialogFactory = feedbackDialogFactory,
                syncService = syncService,
                notificationSchedulingService = notificationSchedulingService,
                notificationService = notificationService,
                feedbackService = feedbackService,
                settingsGateway = settingsGateway,
                conferenceRepository = conferenceRepository,
            )
            notificationService.setHandler(applicationViewModel)
            return applicationViewModel
        }
    }
}
