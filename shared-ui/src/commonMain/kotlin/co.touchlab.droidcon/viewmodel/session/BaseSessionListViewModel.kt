package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.toConferenceDateTime
import kotlinx.coroutines.flow.collect
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

abstract class BaseSessionListViewModel(
    private val sessionGateway: SessionGateway,
    private val sessionDayFactory: SessionDayViewModel.Factory,
    private val sessionDetailFactory: SessionDetailViewModel.Factory,
    private val sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
    private val dateTimeService: DateTimeService,
    val attendingOnly: Boolean,
): BaseViewModel() {

    var days: List<SessionDayViewModel>? by published(null)
        private set
    val observeDays by observe(::days)

    var selectedDay: SessionDayViewModel? by managed(days?.firstOrNull { it.date == sessionDetailScrollStateStorage.selectedDay })
    val observeSelectedDay by observe(::selectedDay)

    var presentedSessionDetail: SessionDetailViewModel? by managed(null)
    val observePresentedSessionDetail by observe(::presentedSessionDetail)

    override suspend fun whileAttached() {
        val itemsFlow = if (attendingOnly) {
            sessionGateway.observeAgenda()
        } else {
            sessionGateway.observeSchedule()
        }

        itemsFlow
            .collect { items ->
                items
                    .groupBy { it.session.startsAt.toConferenceDateTime(dateTimeService).date }
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
