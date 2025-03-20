package co.touchlab.droidcon.viewmodel

import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.SyncService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class WaitForLoadedContextModel(
    private val conferenceConfigProvider: ConferenceConfigProvider,
    applicationViewModelFactory: ApplicationViewModel.Factory,
    private val syncService: SyncService,
    private val settingsGateway: SettingsGateway,
) : BaseViewModel() {
    sealed interface State {
        data object Loading : State
        data class Ready(val conference: Conference) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state
    val applicationViewModel by managed(applicationViewModelFactory.create())

    suspend fun monitorConferenceChanges() {
        conferenceConfigProvider.loadSelectedConference()
    }

    suspend fun watchConferenceChanges() {
        val isFirstRun = settingsGateway.settings().value.isFirstRun
        lifecycle.whileAttached {
            if (isFirstRun) {
                withContext(Dispatchers.IO) {
                    syncService.syncConferences()
                }
            } else {
                launch {
                    syncService.syncConferences()
                }
            }
            launch {
                conferenceConfigProvider.observeChanges().collect { conference ->
                    _state.emit(State.Ready(conference))
                }
            }
        }
    }
}
