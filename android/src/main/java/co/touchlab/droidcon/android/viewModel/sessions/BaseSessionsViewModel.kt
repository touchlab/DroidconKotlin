package co.touchlab.droidcon.android.viewModel.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.domain.gateway.SessionGateway
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseSessionsViewModel(
    private val onlyAttending: Boolean,
): ViewModel(), KoinComponent {

    val days = MutableStateFlow(emptyList<SessionsDayViewModel>())

    private val sessionGateway by inject<SessionGateway>()
    private val scope = viewModelScope

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        scope.launch {
            val scheduleItems = if (onlyAttending) {
                sessionGateway.getAgenda()
            } else {
                sessionGateway.getSchedule()
            }
            val days = scheduleItems
                .groupBy { it.session.startsAt.date }
                .map { (date, items) ->
                    SessionsDayViewModel(date, items)
                }
            this@BaseSessionsViewModel.days.value = days
        }
    }
}