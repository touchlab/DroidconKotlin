package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.gateway.SessionGateway
import kotlinx.datetime.TimeZone

class AgendaViewModel(
    sessionGateway: SessionGateway,
    sessionDayFactory: SessionDayViewModel.Factory,
    timeZone: TimeZone,
): BaseSessionListViewModel(
    sessionGateway,
    sessionDayFactory,
    timeZone,
    attendingOnly = true,
) {
    class Factory(
        private val sessionGateway: SessionGateway,
        private val sessionDayFactory: SessionDayViewModel.Factory,
        private val timeZone: TimeZone,
    ) {
        fun create() = AgendaViewModel(sessionGateway, sessionDayFactory, timeZone)
    }
}