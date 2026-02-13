package co.touchlab.droidcon.viewmodel

import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.ui.util.IODispatcher
import co.touchlab.kermit.Logger
import kotlin.js.JsExport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
class WaitForLoadedContextModel(
    private val conferenceConfigProvider: ConferenceConfigProvider,
    applicationViewModelFactory: ViewModelFactory.ApplicationViewModelFactory,
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

    private val log = Logger.withTag("WaitForLoadedContextModel")

    suspend fun monitorConferenceChanges() {
        conferenceConfigProvider.loadSelectedConference()
    }

    suspend fun watchConferenceChanges() {
        log.i { "watchConferenceChanges" }
        lifecycle.whileAttached {
            log.i { "Starting to Sync Conferences" }
            launch {
                log.i { "Observing conferenceConfigProvider" }
                conferenceConfigProvider.observeChanges().collect { conference ->
                    if (conference != null) {
                        log.i { "WaitForLoadedContextModel: Emitting Conference!" }
                        _state.emit(State.Ready(conference))

                        withContext(IODispatcher) {
                            try {
                                log.i { "syncConferences" }
                                syncService.syncConferences()
                            } catch (e: Exception) {
                                log.e(e) { "Failed to sync conferences" }
                            }
                        }
                    }
                }
            }
        }
    }
}
