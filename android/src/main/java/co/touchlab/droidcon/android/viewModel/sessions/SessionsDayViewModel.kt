package co.touchlab.droidcon.android.viewModel.sessions

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.android.service.DateTimeFormatterViewService
import co.touchlab.droidcon.android.util.startOfMinute
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.toConferenceDateTime
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
        .groupBy { it.session.startsAt }
        .map { (startsAt, items) ->
            SessionsBlockViewModel(
                startsAt = startsAt,
                // Only once all items have ended we consider the block to end.
                endsAt = items.maxOf { it.session.endsAt },
                items = items
            )
        }
}