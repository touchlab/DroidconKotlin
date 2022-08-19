package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.util.DcDispatchers
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.first

class AgendaComponent(
    componentContext: ComponentContext,
    dispatchers: DcDispatchers,
    sessionGateway: SessionGateway,
    sessionDaysFactory: SessionDaysComponent.Factory,
    dateTimeService: DateTimeService,
    sessionSelected: (Session.Id) -> Unit,
): BaseSessionListComponent(
    componentContext = componentContext,
    dispatchers = dispatchers,
    showAttendingIndicators = false,
    scheduleItems = { sessionGateway.observeAgenda().first() },
    sessionDaysFactory = sessionDaysFactory,
    dateTimeService = dateTimeService,
    sessionSelected = sessionSelected,
) {

    class Factory(
        private val dispatchers: DcDispatchers,
        private val sessionGateway: SessionGateway,
        private val sessionDaysFactory: SessionDaysComponent.Factory,
        private val dateTimeService: DateTimeService,
    ) {

        fun create(
            componentContext: ComponentContext,
            sessionSelected: (Session.Id) -> Unit,
        ) = AgendaComponent(componentContext, dispatchers, sessionGateway, sessionDaysFactory, dateTimeService, sessionSelected)
    }
}
