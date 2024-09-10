package co.touchlab.droidcon.viewmodel

import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.application.service.Notification
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.FeedbackService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.service.DeepLinkNotificationHandler
import co.touchlab.droidcon.viewmodel.session.AgendaViewModel
import co.touchlab.droidcon.viewmodel.session.ScheduleViewModel
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorListViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class ApplicationViewModel(
    scheduleFactory: ScheduleViewModel.Factory,
    agendaFactory: AgendaViewModel.Factory,
    sponsorsFactory: SponsorListViewModel.Factory,
    settingsFactory: SettingsViewModel.Factory,
    private val feedbackDialogFactory: FeedbackDialogViewModel.Factory,
    private val syncService: SyncService,
    private val notificationSchedulingService: NotificationSchedulingService,
    private val feedbackService: FeedbackService,
    private val settingsGateway: SettingsGateway,
) : BaseViewModel(), DeepLinkNotificationHandler {

    val schedule by managed(scheduleFactory.create())
    val agenda by managed(agendaFactory.create())
    val sponsors by managed(sponsorsFactory.create())
    val settings by managed(settingsFactory.create())

    var useCompose: Boolean by binding(
        settingsGateway.settings(),
        mapping = { it.useComposeForIos },
        set = { newValue ->
            // TODO: Remove when `binding` supports suspend closures.
            instanceLock.runExclusively {
                settingsGateway.setUseComposeForIos(newValue)
            }
        }
    )

    var presentedFeedback: FeedbackDialogViewModel? by managed(null)
    val observePresentedFeedback by observe(::presentedFeedback)

    val tabs = listOf(Tab.Schedule, Tab.MyAgenda, Tab.Venue, Tab.Sponsors, Tab.Settings)
    var selectedTab: Tab by published(Tab.Schedule)
    val observeSelectedTab by observe(::selectedTab)

    val showSplashScreen = MutableStateFlow(true)

    init {
        lifecycle.whileAttached {
            notificationSchedulingService.runScheduling()
        }

        lifecycle.whileAttached {
            syncService.runSynchronization()
        }
    }

    override fun handleDeepLinkNotification(notification: Notification.DeepLink) {
        when (notification) {
            is Notification.Local.Feedback ->
                lifecycle.whileAttached {
                    // We're not checking whether feedback is enabled, because the user opened a feedback notification.
                    presentNextFeedback()
                }
            is Notification.Local.Reminder -> {
                selectedTab = Tab.Schedule
                schedule.openSessionDetail(notification.sessionId)
            }
        }
    }

    fun onAppear() {
        lifecycle.whileAttached {
            if (settingsGateway.settings().value.isFeedbackEnabled) {
                presentNextFeedback()
            }
        }
    }

    private suspend fun presentNextFeedback() {
        presentedFeedback = feedbackService.next()?.let { session ->
            feedbackDialogFactory.create(
                session,
                submit = { feedback ->
                    feedbackService.submit(session, feedback)
                    presentNextFeedback()
                },
                closeAndDisable = {
                    settingsGateway.setFeedbackEnabled(false)
                    presentedFeedback = null
                },
                skip = {
                    feedbackService.skip(session)
                    presentNextFeedback()
                },
            )
        }
    }

    enum class Tab {
        Schedule, MyAgenda, Venue, Sponsors, Settings;
    }
}
