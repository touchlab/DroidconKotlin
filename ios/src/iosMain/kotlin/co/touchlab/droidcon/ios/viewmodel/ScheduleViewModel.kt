package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

class ScheduleViewModel(
    sessionGateway: SessionGateway,
    sessionDayFactory: SessionDayViewModel.Factory,
    sessionDetailFactory: SessionDetailViewModel.Factory,
    dateTimeService: DateTimeService,
): BaseSessionListViewModel(
    sessionGateway,
    sessionDayFactory,
    sessionDetailFactory,
    dateTimeService,
    attendingOnly = false,
) {
    class Factory(
        private val sessionGateway: SessionGateway,
        private val sessionDayFactory: SessionDayViewModel.Factory,
        private val sessionDetailFactory: SessionDetailViewModel.Factory,
        private val dateTimeService: DateTimeService,
    ) {
        fun create() = ScheduleViewModel(sessionGateway, sessionDayFactory, sessionDetailFactory, dateTimeService)
    }
}
