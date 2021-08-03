package co.touchlab.droidcon.android.viewModel.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.toConferenceDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseSessionsViewModel(
    private val onlyAttending: Boolean,
): ViewModel(), KoinComponent {

    val days = MutableStateFlow(emptyList<SessionsDayViewModel>())

    private val sessionGateway by inject<SessionGateway>()
    private val dateTimeService by inject<DateTimeService>()
    private val scope = viewModelScope

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        scope.launch {
            val itemsFlow = if (onlyAttending) {
                sessionGateway.observeAgenda()
            } else {
                sessionGateway.observeSchedule()
            }
            itemsFlow.collect { items ->
                val days = items.groupBy { it.session.startsAt.toConferenceDateTime(dateTimeService).date }
                .map { (date, items) ->
                    SessionsDayViewModel(date, items)
                }
                this@BaseSessionsViewModel.days.value = days
            }

        }
    }
}