package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.service.DateTimeService
import kotlinx.datetime.Clock
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SessionListItemViewModel(
    dateTimeService: DateTimeService,
    clock: Clock,
    item: ScheduleItem,
    val selected: () -> Unit,
): BaseViewModel() {
    val title: String = item.session.title
    val isServiceSession: Boolean = item.session.isServiceSession
    val isAttending: Boolean = item.session.isAttending
    val isInConflict: Boolean = item.isInConflict
    val isInPast: Boolean = item.session.endsAt < clock.now()
    val speakers: String = item.speakers.joinToString { it.fullName }

    class Factory(
        private val dateTimeService: DateTimeService,
        private val clock: Clock,
    ) {
        fun create(item: ScheduleItem, selected: () -> Unit) = SessionListItemViewModel(dateTimeService, clock, item, selected)
    }
}