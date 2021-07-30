package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.ios.util.formatter.DateFormatter
import kotlinx.datetime.LocalDateTime
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SessionBlockViewModel(
    sessionListItemFactory: SessionListItemViewModel.Factory,
    sessionDetailFactory: SessionDetailViewModel.Factory,
    dateFormatter: DateFormatter,
    startsAt: LocalDateTime,
    items: List<ScheduleItem>,
): BaseViewModel() {
    val time: String = dateFormatter.timeOnly(startsAt) ?: ""
    val sessions: List<SessionListItemViewModel> by managedList(
        items.map {
            sessionListItemFactory.create(it, selected = {
                presentedSessionDetail = sessionDetailFactory.create(it)
            })
        }
    )

    var presentedSessionDetail: SessionDetailViewModel? by managed(null)

    class Factory(
        private val sessionListItemFactory: SessionListItemViewModel.Factory,
        private val sessionDetailFactory: SessionDetailViewModel.Factory,
        private val dateFormatter: DateFormatter,
    ) {
        fun create(
            startsAt: LocalDateTime,
            items: List<ScheduleItem>,
        ) = SessionBlockViewModel(sessionListItemFactory, sessionDetailFactory, dateFormatter, startsAt, items)
    }
}