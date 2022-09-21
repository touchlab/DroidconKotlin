package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.toConferenceDateTime
import co.touchlab.droidcon.util.formatter.DateFormatter
import co.touchlab.droidcon.util.startOfMinute
import kotlinx.datetime.LocalDate
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SessionDayViewModel(
    sessionBlockFactory: SessionBlockViewModel.Factory,
    dateFormatter: DateFormatter,
    dateTimeService: DateTimeService,
    private val sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
    private val date: LocalDate,
    private val attendingOnly: Boolean,
    items: List<ScheduleItem>,
    onScheduleItemSelected: (ScheduleItem) -> Unit,
): BaseViewModel() {

    val day: String = dateFormatter.monthWithDay(date) ?: ""
    val blocks: List<SessionBlockViewModel> by managedList(
        items
            .groupBy { it.session.startsAt.toConferenceDateTime(dateTimeService).startOfMinute }
            .map { (startsAt, items) ->
                sessionBlockFactory.create(startsAt, items, onScheduleItemSelected)
            }
    )

    var scrollState: ScrollState
        get() = sessionDetailScrollStateStorage.getScrollState(date, attendingOnly)
        set(value) {
            sessionDetailScrollStateStorage.setScrollState(date, attendingOnly, value)
        }

    class ScrollState(val firstVisibleItemIndex: Int, val firstVisibleItemScrollOffset: Int)

    class Factory(
        private val sessionBlockFactory: SessionBlockViewModel.Factory,
        private val dateFormatter: DateFormatter,
        private val dateTimeService: DateTimeService,
        private val sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
    ) {

        fun create(
            date: LocalDate,
            attendingOnly: Boolean,
            items: List<ScheduleItem>,
            onScheduleItemSelected: (ScheduleItem) -> Unit,
        ) = SessionDayViewModel(
            sessionBlockFactory,
            dateFormatter,
            dateTimeService,
            sessionDetailScrollStateStorage,
            date,
            attendingOnly,
            items,
            onScheduleItemSelected,
        )
    }
}
