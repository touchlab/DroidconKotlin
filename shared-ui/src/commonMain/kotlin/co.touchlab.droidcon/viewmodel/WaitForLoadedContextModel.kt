package co.touchlab.droidcon.viewmodel

import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class WaitForLoadedContextModel(
    private val conferenceConfigProvider: ConferenceConfigProvider,
    applicationViewModelFactory: ApplicationViewModel.Factory,
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

    fun watchConferenceChanges(scope: CoroutineScope) {
        scope.launch {
            conferenceConfigProvider.observeChanges().collect { conference ->
                _state.emit(State.Loading)
                delay(300)
                _state.emit(State.Ready(conference))
            }
        }
    }
}
