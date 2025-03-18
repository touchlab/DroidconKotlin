package co.touchlab.droidcon.viewmodel

import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

class WaitForLoadedContextModel(private val conferenceConfigProvider: ConferenceConfigProvider) {
    enum class State {
        Loading, Ready
    }

    private val _state = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state

    suspend fun monitorConferenceChanges() {
        conferenceConfigProvider.loadSelectedConference()
    }

    suspend fun waitTillReady(){
        conferenceConfigProvider.observeChanges().first()
        _state.emit(State.Ready)
    }
}
