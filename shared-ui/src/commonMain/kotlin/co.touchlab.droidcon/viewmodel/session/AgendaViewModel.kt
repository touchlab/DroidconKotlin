package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.viewmodel.ViewModelFactory

class AgendaViewModel(
    sessionGateway: SessionGateway,
    sessionDayFactory: ViewModelFactory.SessionDayViewModelFactory,
    sessionDetailFactory: ViewModelFactory.SessionDetailViewModelFactory,
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
)
