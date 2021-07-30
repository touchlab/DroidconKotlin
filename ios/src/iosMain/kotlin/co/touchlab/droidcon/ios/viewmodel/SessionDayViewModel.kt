package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.ios.util.formatter.DateFormatter
import co.touchlab.droidcon.ios.util.startOfMinute
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SessionDayViewModel(
    sessionBlockFactory: SessionBlockViewModel.Factory,
    dateFormatter: DateFormatter,
    timeZone: TimeZone,
    date: LocalDate,
    items: List<ScheduleItem>,
): BaseViewModel() {

    val day: String = dateFormatter.monthWithDay(date) ?: ""
    val blocks: List<SessionBlockViewModel> by managedList(
        items
            .groupBy { it.session.startsAt.toLocalDateTime(timeZone).startOfMinute }
            .map { (startsAt, items) ->
                sessionBlockFactory.create(startsAt, items)
            }
    )

    class Factory(
        private val sessionBlockFactory: SessionBlockViewModel.Factory,
        private val dateFormatter: DateFormatter,
        private val timeZone: TimeZone,
    ) {
        fun create(
            date: LocalDate,
            items: List<ScheduleItem>,
        ) = SessionDayViewModel(sessionBlockFactory, dateFormatter, timeZone, date, items)
    }
}