package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.gateway.SessionGateway
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

abstract class BaseSessionListViewModel(
    private val sessionGateway: SessionGateway,
    private val sessionDayFactory: SessionDayViewModel.Factory,
    private val timeZone: TimeZone,
    val attendingOnly: Boolean,
): BaseViewModel() {

    var days: List<SessionDayViewModel> by published(emptyList())
        private set

    var selectedDay: SessionDayViewModel? by managed(null)

    override suspend fun whileAttached() {
        val itemsFlow = if (attendingOnly) {
            sessionGateway.observeAgenda()
        } else {
            sessionGateway.observeAgenda()
        }

        itemsFlow
            .collect { items ->
                days = items
                    .groupBy { it.session.startsAt.toLocalDateTime(timeZone).date }
                    .map { (date, items) ->
                        sessionDayFactory.create(date, items)
                    }

                if (selectedDay == null) {
                    selectedDay = days.firstOrNull()
                }
            }
    }
}