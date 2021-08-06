package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.ios.util.formatter.DateFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import org.brightify.hyperdrive.multiplatformx.CancellationToken
import org.brightify.hyperdrive.multiplatformx.property.ViewModelProperty
import org.brightify.hyperdrive.multiplatformx.property.map

class SessionBlockViewModel(
    sessionListItemFactory: SessionListItemViewModel.Factory,
    dateFormatter: DateFormatter,
    startsAt: LocalDateTime,
    items: List<ScheduleItem>,
    onScheduleItemSelected: (ScheduleItem) -> Unit,
): BaseViewModel() {
    val time: String = dateFormatter.timeOnly(startsAt) ?: ""
    val sessions: List<SessionListItemViewModel> by managedList(
        items.map { item ->
            sessionListItemFactory.create(item, selected = {
                onScheduleItemSelected(item)
            })
        }
    )

    class Factory(
        private val sessionListItemFactory: SessionListItemViewModel.Factory,
        private val dateFormatter: DateFormatter,
    ) {
        fun create(
            startsAt: LocalDateTime,
            items: List<ScheduleItem>,
            onScheduleItemSelected: (ScheduleItem) -> Unit,
        ) = SessionBlockViewModel(sessionListItemFactory, dateFormatter, startsAt, items, onScheduleItemSelected)
    }
}
