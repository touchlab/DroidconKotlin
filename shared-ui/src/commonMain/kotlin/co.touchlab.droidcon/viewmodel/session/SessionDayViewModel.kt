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
    date: LocalDate,
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

    class Factory(
        private val sessionBlockFactory: SessionBlockViewModel.Factory,
        private val dateFormatter: DateFormatter,
        private val dateTimeService: DateTimeService,
    ) {

        fun create(
            date: LocalDate,
            items: List<ScheduleItem>,
            onScheduleItemSelected: (ScheduleItem) -> Unit,
        ) = SessionDayViewModel(sessionBlockFactory, dateFormatter, dateTimeService, date, items, onScheduleItemSelected)
    }
}
