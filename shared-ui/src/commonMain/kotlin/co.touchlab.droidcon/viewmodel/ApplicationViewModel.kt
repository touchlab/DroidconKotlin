package co.touchlab.droidcon.viewmodel

import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.application.service.Notification
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.FeedbackService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.service.DeepLinkNotificationHandler
import co.touchlab.droidcon.viewmodel.session.AgendaViewModel
import co.touchlab.droidcon.viewmodel.session.ScheduleViewModel
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorListViewModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import org.brightify.hyperdrive.multiplatformx.property.MutableObservableProperty
import org.brightify.hyperdrive.multiplatformx.property.ObservableProperty

class ApplicationViewModel(
    scheduleFactory: ScheduleViewModel.Factory,
    agendaFactory: AgendaViewModel.Factory,
    sponsorsFactory: SponsorListViewModel.Factory,
    settingsFactory: SettingsViewModel.Factory,
    private val feedbackDialogFactory: FeedbackDialogViewModel.Factory,
    private val syncService: SyncService,
    private val notificationSchedulingService: NotificationSchedulingService,
    private val notificationService: NotificationService,
    private val feedbackService: FeedbackService,
    private val settingsGateway: SettingsGateway,
    private val conference: Conference,
    private val conferenceRepository: ConferenceRepository,
    // private val conferenceConfigProvider: ConferenceConfigProvider,
) : BaseViewModel(),
    DeepLinkNotificationHandler {

    class Factory(
        private val scheduleFactory: ScheduleViewModel.Factory,
        private val agendaFactory: AgendaViewModel.Factory,
        private val sponsorsFactory: SponsorListViewModel.Factory,
        private val settingsFactory: SettingsViewModel.Factory,
        private val feedbackDialogFactory: FeedbackDialogViewModel.Factory,
        private val syncService: SyncService,
        private val notificationSchedulingService: NotificationSchedulingService,
        private val notificationService: NotificationService,
        private val feedbackService: FeedbackService,
        private val settingsGateway: SettingsGateway,
        private val conferenceRepository: ConferenceRepository,
        // private val conferenceConfigProvider: ConferenceConfigProvider,
    ) {

        fun create(conference:Conference): ApplicationViewModel {
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
                conference = conference,
                conferenceRepository = conferenceRepository,
                // conferenceConfigProvider = conferenceConfigProvider,
            )
            notificationService.setHandler(applicationViewModel)
            return applicationViewModel
        }
    }

    private val log = Logger.withTag("ApplicationViewModel")

    val schedule by managed(scheduleFactory.create())
    val agenda by managed(agendaFactory.create())
    val sponsors by managed(sponsorsFactory.create())
    val settings by managed(settingsFactory.create())

    var presentedFeedback: FeedbackDialogViewModel? by managed(null)
    val observePresentedFeedback by observe(::presentedFeedback)

    // Add state properties to track conferences
    private val _currentConference = MutableStateFlow<Conference?>(null)
    val currentConference = _currentConference.asStateFlow()

    private val _allConferences = MutableObservableProperty<List<Conference>>(emptyList())
    val allConferences: ObservableProperty<List<Conference>> = _allConferences
    val observeAllConferences by observe(::allConferences)

    private val _isFirstRun = MutableObservableProperty<Boolean>(true)
    val isFirstRun: ObservableProperty<Boolean> = _isFirstRun
    val observeIsFirstRun by observe(::isFirstRun)

    val tabs = listOfNotNull(
        Tab.Schedule,
        Tab.MyAgenda,
        if (conference.showVenueMap) Tab.Venue else null,
        Tab.Sponsors,
        Tab.Settings,
    )
    var selectedTab: Tab by published(Tab.Schedule)
    val observeSelectedTab by observe(::selectedTab)

    val showSplashScreen = MutableStateFlow(true)

    init {
        log.i { "ApplicationViewModel initialization starting" }

        lifecycle.whileAttached {
            log.d { "Starting notification scheduling service" }
            try {
                notificationSchedulingService.runScheduling()
                log.d { "Notification scheduling service started successfully" }
            } catch (e: Exception) {
                log.e(e) { "Error starting notification scheduling service" }
            }
        }

        lifecycle.whileAttached {
            log.d { "Starting sync service" }
            try {
                syncService.runSynchronization(conference = conference)
                log.d { "Sync service started successfully" }
            } catch (e: Exception) {
                log.e(e) { "Error starting sync service" }
            }
        }

        // Initialize conferences - in a non-blocking way
        lifecycle.whileAttached {
            log.d { "Initializing conferences asynchronously" }
            try {
                // First load all conferences
                log.d { "Starting to observe all conferences" }
                conferenceRepository.observeAll().collect { conferences ->
                    log.d { "Received ${conferences.size} conferences from repository" }
                    _allConferences.value = conferences
                }
            } catch (e: Exception) {
                log.e(e) { "Error initializing conferences" }
            }
        }

        // Observe first run status
        lifecycle.whileAttached {
            log.d { "Starting to observe first run status" }
            try {
                val isFirstRunValue = settingsGateway.settings().value.isFirstRun
                log.d { "First run status: $isFirstRunValue" }
                _isFirstRun.value = isFirstRunValue
            } catch (e: Exception) {
                log.e(e) { "Error observing first run status" }
            }
        }

        lifecycle.whileAttached {
            log.d { "Processing onAppear" }
            try {
                val feedbackEnabled = settingsGateway.settings().value.isFeedbackEnabled
                log.d { "Feedback enabled: $feedbackEnabled" }
                if (feedbackEnabled) {
                    log.d { "Presenting next feedback" }
                    presentNextFeedback()
                }
            } catch (e: Exception) {
                log.e(e) { "Error in onAppear" }
            }
        }

        log.i { "ApplicationViewModel initialization completed" }
    }

    override fun handleDeepLinkNotification(notification: Notification.DeepLink) {
        log.d { "Handling deep link notification: $notification" }
        when (notification) {
            is Notification.Local.Feedback ->
                lifecycle.whileAttached {
                    log.d { "Processing feedback notification" }
                    // We're not checking whether feedback is enabled, because the user opened a feedback notification.
                    try {
                        presentNextFeedback()
                        log.d { "Feedback notification processed successfully" }
                    } catch (e: Exception) {
                        log.e(e) { "Error processing feedback notification" }
                    }
                }

            is Notification.Local.Reminder -> {
                log.d { "Processing reminder notification for session ${notification.sessionId}" }
                selectedTab = Tab.Schedule
                schedule.openSessionDetail(notification.sessionId)
                log.d { "Reminder notification processed" }
            }
        }
    }

    // Function to set the selected conference
    fun selectConference(conferenceId: Long) {
        log.d { "selectConference called with conferenceId: $conferenceId" }
        lifecycle.whileAttached {
            log.d { "Processing conference selection" }
            try {
                log.d { "Selecting conference with ID: $conferenceId" }
                conferenceRepository.select(conferenceId)
                log.d { "Conference selected successfully" }

                // Mark first run complete after conference selection
                log.d { "Setting first run to false" }
                settingsGateway.setFirstRun(false)
                log.d { "First run set to false successfully" }
            } catch (e: Exception) {
                log.e(e) { "Error selecting conference" }
            }
        }
    }

    private suspend fun presentNextFeedback() {
        log.d { "presentNextFeedback called" }
        try {
            val nextSession = feedbackService.next()
            if (nextSession != null) {
                log.d { "Found feedback session: ${nextSession.id}" }
                presentedFeedback = feedbackDialogFactory.create(
                    nextSession,
                    submit = { feedback ->
                        log.d { "Submitting feedback for session ${nextSession.id}" }
                        feedbackService.submit(nextSession, feedback)
                        log.d { "Cancelling notification for session ${nextSession.id}" }
                        notificationService.cancel(listOf(nextSession.id))
                        log.d { "Checking for next feedback" }
                        presentNextFeedback()
                    },
                    closeAndDisable = {
                        log.d { "User disabled feedback" }
                        settingsGateway.setFeedbackEnabled(false)
                        presentedFeedback = null
                        log.d { "Feedback disabled" }
                    },
                    skip = {
                        log.d { "User skipped feedback for session ${nextSession.id}" }
                        feedbackService.skip(nextSession)
                        log.d { "Checking for next feedback" }
                        presentNextFeedback()
                    },
                )
                log.d { "Feedback view model created and assigned" }
            } else {
                log.d { "No feedback sessions available" }
                presentedFeedback = null
            }
        } catch (e: Exception) {
            log.e(e) { "Error presenting feedback" }
            presentedFeedback = null
        }
    }

    enum class Tab {
        Schedule,
        MyAgenda,
        Venue,
        Sponsors,
        Settings,
    }
}
