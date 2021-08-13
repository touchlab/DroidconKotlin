package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.ios.viewmodel.session.AgendaViewModel
import co.touchlab.droidcon.ios.viewmodel.session.ScheduleViewModel
import co.touchlab.droidcon.ios.viewmodel.settings.SettingsViewModel
import co.touchlab.droidcon.ios.viewmodel.sponsor.SponsorListViewModel
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class ApplicationViewModel(
    scheduleFactory: ScheduleViewModel.Factory,
    agendaFactory: AgendaViewModel.Factory,
    sponsorsFactory: SponsorListViewModel.Factory,
    settingsFactory: SettingsViewModel.Factory,
    private val feedbackDialogFactory: FeedbackDialogViewModel.Factory,
    private val syncService: SyncService,
    private val notificationSchedulingService: NotificationSchedulingService,
): BaseViewModel() {
    val schedule by managed(scheduleFactory.create())
    val agenda by managed(agendaFactory.create())
    val sponsors by managed(sponsorsFactory.create())
    val settings by managed(settingsFactory.create())

    var presentedFeedback: FeedbackDialogViewModel? by managed(null)

    init {
        lifecycle.whileAttached {
            notificationSchedulingService.runScheduling()
        }

        lifecycle.whileAttached {
            syncService.runSynchronization()
        }
    }
}