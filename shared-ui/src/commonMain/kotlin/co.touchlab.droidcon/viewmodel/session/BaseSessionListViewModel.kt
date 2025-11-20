package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.toConferenceDateTime

abstract class BaseSessionListViewModel(
    private val sessionGateway: SessionGateway,
    private val sessionDayFactory: SessionDayViewModel.Factory,
    private val sessionDetailFactory: SessionDetailViewModel.Factory,
    private val sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
    private val dateTimeService: DateTimeService,
    private val conferenceConfigProvider: ConferenceConfigProvider,
    val attendingOnly: Boolean,
) : ViewModel() {

    var days: List<SessionDayViewModel>? by published(null)
        private set
    val observeDays by observe(::days)

    var selectedDay: SessionDayViewModel? by managed(days?.firstOrNull { it.date == sessionDetailScrollStateStorage.selectedDay })
    val observeSelectedDay by observe(::selectedDay)

    var presentedSessionDetail: SessionDetailViewModel? by managed(null)
    val observePresentedSessionDetail by observe(::presentedSessionDetail)

    override suspend fun whileAttached() {
        // We don't need to explicitly subscribe to conference changes
        // The Repository/Gateway classes already filter by the current conference
        // So we just need to observe the sessions which will automatically
        // use the current conference ID from conferenceConfigProvider
        val itemsFlow = if (attendingOnly) {
            sessionGateway.observeAgenda()
        } else {
            sessionGateway.observeSchedule()
        }

        itemsFlow
            .collect { items ->
                items
                    .groupBy {
                        it.session.startsAt.toConferenceDateTime(
                            dateTimeService,
                            conferenceConfigProvider.getConferenceTimeZone(),
                        ).date
                    }
                    .map { (date, items) ->
                        sessionDayFactory.create(date, attendingOnly, items) { item ->
                            if (item.session.isServiceSession) {
                                return@create
                            }
                            presentedSessionDetail = sessionDetailFactory.create(item)
                        }
                    }
                    .also { newDays ->
                        days = newDays
                        selectedDay = newDays.firstOrNull { it.day == selectedDay?.day } ?: newDays.firstOrNull()
                    }
            }
    }
}
