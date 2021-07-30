package co.touchlab.droidcon.android.viewModel.sessions

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.android.service.DateTimeFormatterViewService
import co.touchlab.droidcon.android.util.startOfMinute
import co.touchlab.droidcon.domain.composite.ScheduleItem
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SessionsDayViewModel(
    date: LocalDate,
    items: List<ScheduleItem>,
): ViewModel(), KoinComponent {

    private val dateTimeFormatter by inject<DateTimeFormatterViewService>()

    val day: String = dateTimeFormatter.shortDate(date)

    val blocks: List<SessionsBlockViewModel> = items
        .groupBy { it.session.startsAt.startOfMinute }
        .map { (startsAt, items) ->
            SessionsBlockViewModel(startsAt, items)
        }
}