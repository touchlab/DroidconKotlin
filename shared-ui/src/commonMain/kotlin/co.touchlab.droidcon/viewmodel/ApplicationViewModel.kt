package co.touchlab.droidcon.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.application.service.Notification
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.droidcon.domain.service.FeedbackService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.service.DeepLinkNotificationHandler
import co.touchlab.droidcon.viewmodel.binding
import co.touchlab.droidcon.viewmodel.collected
import co.touchlab.droidcon.viewmodel.instanceLock
import co.touchlab.droidcon.viewmodel.lifecycle
import co.touchlab.droidcon.viewmodel.managed
import co.touchlab.droidcon.viewmodel.managedList
import co.touchlab.droidcon.viewmodel.observe
import co.touchlab.droidcon.viewmodel.published
import co.touchlab.droidcon.viewmodel.session.AgendaViewModel
import co.touchlab.droidcon.viewmodel.session.ScheduleViewModel
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorListViewModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    private val conferenceRepository: ConferenceRepository,
) : ViewModel(),
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

    private val log = Logger.withTag("ApplicationViewModel")

    val schedule by managed(scheduleFactory.create())
    val agenda by managed(agendaFactory.create())
    val sponsors by managed(sponsorsFactory.create())
    val settings by managed(settingsFactory.create())

    var presentedFeedback: FeedbackDialogViewModel? by managed(null)
    val observePresentedFeedback by observe(::presentedFeedback)

    private val _allConferences = MutableStateFlow<List<Conference>>(emptyList())
    val allConferences: ObservableProperty<List<Conference>> = _allConferences
    val observeAllConferences by observe(::allConferences)

    private val _isFirstRun = MutableStateFlow<Boolean>(true)
    val isFirstRun: ObservableProperty<Boolean> = _isFirstRun
    val observeIsFirstRun by observe(::isFirstRun)

    fun listTabs(conference: Conference?): List<Tab> = listOfNotNull(
        Tab.Schedule,
        Tab.MyAgenda,
        if (conference?.showVenueMap == true) Tab.Venue else null,
        Tab.Sponsors,
        Tab.Settings,
    )

    var selectedTab: MutableStateFlow<Tab> = MutableStateFlow(Tab.Schedule)
    val observeSelectedTab: StateFlow<Tab> = selectedTab

    val showSplashScreen = MutableStateFlow(true)

    suspend fun runAllLiveTasks(conference: Conference) {
        selectedTab.value = Tab.Schedule

        viewModelScope.launch {
            try {
                notificationSchedulingService.runScheduling()
            } catch (e: Exception) {
                log.e(e) { "Error starting notification scheduling service" }
            }
        }

        viewModelScope.launch {
            try {
                syncService.runSynchronization(conference = conference)
            } catch (e: Exception) {
                log.e(e) { "Error starting sync service" }
            }
        }
        viewModelScope.launch {
            try {
                val feedbackEnabled = settingsGateway.settings().value.isFeedbackEnabled
                if (feedbackEnabled) {
                    presentNextFeedback()
                }
            } catch (e: Exception) {
                log.e(e) { "Error in onAppear" }
            }
        }
    }

    init {
        log.i { "ApplicationViewModel initialization starting" }

        // Initialize conferences - in a non-blocking way
        viewModelScope.launch {
            try {
                // First load all conferences
                conferenceRepository.observeAll().collect { conferences ->
                    _allConferences.value = conferences
                }
            } catch (e: Exception) {
                log.e(e) { "Error initializing conferences" }
            }
        }

        // Observe first run status
        viewModelScope.launch {
            try {
                settingsGateway.settings().collect { settings ->
                    _isFirstRun.value = settings.isFirstRun
                }
            } catch (e: Exception) {
                log.e(e) { "Error observing first run status" }
            }
        }

        log.i { "ApplicationViewModel initialization completed" }
    }

    override fun handleDeepLinkNotification(notification: Notification.DeepLink) {
        when (notification) {
            is Notification.Local.Feedback ->
                viewModelScope.launch {
                    // We're not checking whether feedback is enabled, because the user opened a feedback notification.
                    try {
                        presentNextFeedback()
                    } catch (e: Exception) {
                        log.e(e) { "Error processing feedback notification" }
                    }
                }

            is Notification.Local.Reminder -> {
                selectedTab.value = Tab.Schedule
                schedule.openSessionDetail(notification.sessionId)
            }
        }
    }

    // Function to set the selected conference
    fun selectConference(conferenceId: Long) {
        viewModelScope.launch {
            try {
                conferenceRepository.select(conferenceId)

                // Mark first run complete after conference selection
                settingsGateway.setFirstRun(false)
            } catch (e: Exception) {
                log.e(e) { "Error selecting conference" }
            }
        }
    }

    private suspend fun presentNextFeedback() {
        try {
            val nextSession = feedbackService.next()
            if (nextSession != null) {
                presentedFeedback = feedbackDialogFactory.create(
                    nextSession,
                    submit = { feedback ->
                        feedbackService.submit(nextSession, feedback)
                        notificationService.cancel(listOf(nextSession.id))
                        presentNextFeedback()
                    },
                    closeAndDisable = {
                        settingsGateway.setFeedbackEnabled(false)
                        presentedFeedback = null
                    },
                    skip = {
                        feedbackService.skip(nextSession)
                        presentNextFeedback()
                    },
                )
            } else {
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
