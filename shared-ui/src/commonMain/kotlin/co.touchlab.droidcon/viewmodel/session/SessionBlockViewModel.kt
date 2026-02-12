package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.viewmodel.ViewModelFactory
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.util.formatter.DateFormatter
import kotlinx.datetime.LocalDateTime
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SessionBlockViewModel(
    sessionListItemFactory: ViewModelFactory.SessionListItemViewModelFactory,
    dateFormatter: DateFormatter,
    startsAt: LocalDateTime,
    items: List<ScheduleItem>,
    onScheduleItemSelected: (ScheduleItem) -> Unit,
) : BaseViewModel() {
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
}
