package co.touchlab.droidcon.viewmodel.session
import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.util.formatter.DateFormatter
import kotlinx.datetime.LocalDateTime

class SessionBlockViewModel(
    sessionListItemFactory: SessionListItemViewModel.Factory,
    dateFormatter: DateFormatter,
    startsAt: LocalDateTime,
    items: List<ScheduleItem>,
    onScheduleItemSelected: (ScheduleItem) -> Unit,
) : ViewModel() {
    val time: String = dateFormatter.timeOnly(startsAt) ?: ""
    val sessions: List<SessionListItemViewModel> by managedList(
        items.map { item ->
            sessionListItemFactory.create(
                item,
                selected = {
                    onScheduleItemSelected(item)
                },
            )
        },
    )
    val observeSessions by observe(::sessions)

    class Factory(private val sessionListItemFactory: SessionListItemViewModel.Factory, private val dateFormatter: DateFormatter) {
        fun create(startsAt: LocalDateTime, items: List<ScheduleItem>, onScheduleItemSelected: (ScheduleItem) -> Unit) =
            SessionBlockViewModel(sessionListItemFactory, dateFormatter, startsAt, items, onScheduleItemSelected)
    }
}
