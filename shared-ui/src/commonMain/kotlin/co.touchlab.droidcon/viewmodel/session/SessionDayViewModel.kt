package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.toConferenceDateTime
import co.touchlab.droidcon.util.formatter.DateFormatter
import co.touchlab.droidcon.util.startOfMinute
import kotlinx.datetime.LocalDate

class SessionDayViewModel(
    sessionBlockFactory: SessionBlockViewModel.Factory,
    dateFormatter: DateFormatter,
    dateTimeService: DateTimeService,
    private val conferenceConfigProvider: ConferenceConfigProvider,
    val date: LocalDate,
    private val attendingOnly: Boolean,
    private val sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
    items: List<ScheduleItem>,
    onScheduleItemSelected: (ScheduleItem) -> Unit,
) : ViewModel() {

    val day: String = dateFormatter.monthWithDay(date) ?: ""
    val blocks: List<SessionBlockViewModel> by managedList(
        items
            .groupBy {
                it.session.startsAt.toConferenceDateTime(
                    dateTimeService,
                    conferenceTimeZone = conferenceConfigProvider.getConferenceTimeZone(),
                ).startOfMinute
            }
            .map { (startsAt, items) ->
                sessionBlockFactory.create(startsAt, items, onScheduleItemSelected)
            },
    )
    val observeBlocks by observe(::blocks)

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
        private val conferenceConfigProvider: ConferenceConfigProvider,
        private val sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
    ) {

        fun create(date: LocalDate, attendingOnly: Boolean, items: List<ScheduleItem>, onScheduleItemSelected: (ScheduleItem) -> Unit) =
            SessionDayViewModel(
                sessionBlockFactory,
                dateFormatter,
                dateTimeService,
                conferenceConfigProvider,
                date,
                attendingOnly,
                sessionDetailScrollStateStorage,
                items,
                onScheduleItemSelected,
            )
    }
}
