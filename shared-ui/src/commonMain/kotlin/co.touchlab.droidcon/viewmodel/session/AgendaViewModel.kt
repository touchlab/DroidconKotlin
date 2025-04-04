package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.DateTimeService

class AgendaViewModel(
    sessionGateway: SessionGateway,
    sessionDayFactory: SessionDayViewModel.Factory,
    sessionDetailFactory: SessionDetailViewModel.Factory,
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
    attendingOnly = true,
) {
    class Factory(
        private val sessionGateway: SessionGateway,
        private val sessionDayFactory: SessionDayViewModel.Factory,
        private val sessionDetailFactory: SessionDetailViewModel.Factory,
        private val sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
        private val dateTimeService: DateTimeService,
        private val conferenceConfigProvider: ConferenceConfigProvider,
    ) {

        fun create() = AgendaViewModel(
            sessionGateway,
            sessionDayFactory,
            sessionDetailFactory,
            sessionDetailScrollStateStorage,
            dateTimeService,
            conferenceConfigProvider,
        )
    }
}
