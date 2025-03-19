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
    private val applicationViewModelFactory: ApplicationViewModel.Factory,
) : BaseViewModel() {
    sealed interface State {
        data object Loading : State
        data class Ready(val conference: Conference) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state
    private var vm: ApplicationViewModel? = null

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

     fun replaceViewModel(conference: Conference?): ApplicationViewModel? {
        this.vm?.let {
            lifecycle.removeChild(it.lifecycle)
            this.vm = null
        }
        return conference?.let { conf ->
            val vm = applicationViewModelFactory.create(conf)
            this.vm = vm
            lifecycle.addChild(vm.lifecycle)
            vm
        }
    }

    fun createApplicationViewModel(conference: Conference): ApplicationViewModel {
        val vm = applicationViewModelFactory.create(conference)
        this.vm = vm
        lifecycle.addChild(vm.lifecycle)
        return vm
    }
}
