package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.viewmodel.ViewModelFactory
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.DateTimeService

class ScheduleViewModel(
    private val sessionGateway: SessionGateway,
    sessionDayFactory: ViewModelFactory.SessionDayViewModelFactory,
    private val sessionDetailFactory: ViewModelFactory.SessionDetailViewModelFactory,
    sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
    dateTimeService: DateTimeService,
    conferenceConfigProvider: ConferenceConfigProvider,
) : BaseSessionListViewModel(
    sessionGateway,
    sessionDayFactory,
    sessionDetailFactory,
    sessionDetailScrollStateStorage,
    dateTimeService,
    conferenceConfigProvider,
    attendingOnly = false,
) {

    fun openSessionDetail(sessionId: Session.Id) {
        lifecycle.whileAttached {
            val sessionItem = sessionGateway.getScheduleItem(sessionId) ?: return@whileAttached
            presentedSessionDetail = sessionDetailFactory.create(sessionItem)
        }
    }
}
