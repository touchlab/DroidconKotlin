package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.ios.viewmodel.AgendaViewModel
import co.touchlab.droidcon.ios.viewmodel.ScheduleViewModel
import co.touchlab.droidcon.ios.viewmodel.SettingsViewModel
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class ApplicationViewModel(
    scheduleFactory: ScheduleViewModel.Factory,
    agendaFactory: AgendaViewModel.Factory,
    settingsFactory: SettingsViewModel.Factory,
    private val syncService: SyncService,
): BaseViewModel() {
    val schedule by managed(scheduleFactory.create())
    val agenda by managed(agendaFactory.create())
    val settings by managed(settingsFactory.create())

    override suspend fun whileAttached() {
        syncService.runSynchronization()
        // TODO: Run notification service autosubscribe.
    }
}