package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.toConferenceDateTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SessionListItemViewModel(
    dateTimeService: DateTimeService,
    item: ScheduleItem,
    val selected: () -> Unit,
): BaseViewModel() {
    val title: String = item.session.title
    val isServiceSession: Boolean = item.session.isServiceSession
    val isAttending: Boolean = item.session.isAttending
    val isInConflict: Boolean = item.isInConflict
    val speakers: String = item.speakers.joinToString { it.fullName }

    val isInPast: Boolean by collected(dateTimeService.now() > item.session.endsAt, flow {
        while (true) {
            val isInPast = dateTimeService.now() > item.session.endsAt
            emit(isInPast)
            delay(10_000)
        }
    })

    class Factory(
        private val dateTimeService: DateTimeService,
    ) {
        fun create(
            item: ScheduleItem,
            selected: () -> Unit,
        ) = SessionListItemViewModel(dateTimeService, item, selected)
    }
}
