package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.toConferenceDateTime
import co.touchlab.droidcon.util.formatter.DateFormatter
import co.touchlab.droidcon.util.startOfMinute
import co.touchlab.droidcon.viewmodel.ViewModelFactory
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SessionDayViewModel(
    sessionBlockFactory: ViewModelFactory.SessionBlockViewModelFactory,
    dateFormatter: DateFormatter,
    dateTimeService: DateTimeService,
    private val conferenceConfigProvider: ConferenceConfigProvider,
    val date: LocalDate,
    private val attendingOnly: Boolean,
    private val sessionDetailScrollStateStorage: SessionDetailScrollStateStorage,
    items: List<ScheduleItem>,
    onScheduleItemSelected: (ScheduleItem) -> Unit,
) : BaseViewModel() {

    val day: String = dateFormatter.monthWithDay(date) ?: ""
    val blocks: List<SessionBlockViewModel> by managedList(
        items
            .groupBy {
                it.session.startsAt.toConferenceDateTime(
                    dateTimeService,
                    conferenceTimeZone = conferenceConfigProvider.getConferenceTimeZone() ?: TimeZone.UTC,
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
}
