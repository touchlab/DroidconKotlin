package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService

class AgendaViewModel(
    sessionGateway: SessionGateway,
    sessionDayFactory: SessionDayViewModel.Factory,
    sessionDetailFactory: SessionDetailViewModel.Factory,
    dateTimeService: DateTimeService,
): BaseSessionListViewModel(
    sessionGateway,
    sessionDayFactory,
    sessionDetailFactory,
    dateTimeService,
    attendingOnly = true,
) {
    class Factory(
        private val sessionGateway: SessionGateway,
        private val sessionDayFactory: SessionDayViewModel.Factory,
        private val sessionDetailFactory: SessionDetailViewModel.Factory,
        private val dateTimeService: DateTimeService,
    ) {
        fun create() = AgendaViewModel(sessionGateway, sessionDayFactory, sessionDetailFactory, dateTimeService)
    }
}
