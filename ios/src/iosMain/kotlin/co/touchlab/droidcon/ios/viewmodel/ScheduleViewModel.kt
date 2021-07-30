package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.gateway.SessionGateway
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

class ScheduleViewModel(
    sessionGateway: SessionGateway,
    sessionDayFactory: SessionDayViewModel.Factory,
    timeZone: TimeZone,
): BaseSessionListViewModel(
    sessionGateway,
    sessionDayFactory,
    timeZone,
    attendingOnly = false,
) {
    class Factory(
        private val sessionGateway: SessionGateway,
        private val sessionDayFactory: SessionDayViewModel.Factory,
        private val timeZone: TimeZone,
    ) {
        fun create() = ScheduleViewModel(sessionGateway, sessionDayFactory, timeZone)
    }
}
