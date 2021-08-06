package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.SyncService
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class ApplicationViewModel(
    scheduleFactory: ScheduleViewModel.Factory,
    agendaFactory: AgendaViewModel.Factory,
    sponsorsFactory: SponsorListViewModel.Factory,
    settingsFactory: SettingsViewModel.Factory,
    private val syncService: SyncService,
    private val notificationSchedulingService: NotificationSchedulingService,
): BaseViewModel() {
    val schedule by managed(scheduleFactory.create())
    val agenda by managed(agendaFactory.create())
    val sponsors by managed(sponsorsFactory.create())
    val settings by managed(settingsFactory.create())

    init {
        lifecycle.whileAttached {
            notificationSchedulingService.runScheduling()
        }

        lifecycle.whileAttached {
            syncService.runSynchronization()
        }
    }
}