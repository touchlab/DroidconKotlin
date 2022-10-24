package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import kotlinx.coroutines.flow.first

class ScheduleViewModel(
    private val sessionGateway: SessionGateway,
    sessionDayFactory: SessionDayViewModel.Factory,
    private val sessionDetailFactory: SessionDetailViewModel.Factory,
    sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
    dateTimeService: DateTimeService,
): BaseSessionListViewModel(
    sessionGateway,
    sessionDayFactory,
    sessionDetailFactory,
    sessionDetailScrollStateStorage,
    dateTimeService,
    attendingOnly = false,
) {

    fun openSessionDetail(sessionId: String) {
        lifecycle.whileAttached {
            val sessionItem = sessionGateway.getScheduleItem(Session.Id(sessionId)) ?: return@whileAttached
            presentedSessionDetail = sessionDetailFactory.create(sessionItem)
        }
    }

    class Factory(
        private val sessionGateway: SessionGateway,
        private val sessionDayFactory: SessionDayViewModel.Factory,
        private val sessionDetailFactory: SessionDetailViewModel.Factory,
        private val sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
        private val dateTimeService: DateTimeService,
    ) {

        fun create() =
            ScheduleViewModel(sessionGateway, sessionDayFactory, sessionDetailFactory, sessionDetailScrollStateStorage, dateTimeService)
    }
}
