package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.viewmodel.managed
import co.touchlab.droidcon.viewmodel.managedList
import co.touchlab.droidcon.viewmodel.observe
import co.touchlab.droidcon.viewmodel.published
import co.touchlab.droidcon.viewmodel.binding
import co.touchlab.droidcon.viewmodel.collected
import co.touchlab.droidcon.viewmodel.lifecycle
import co.touchlab.droidcon.viewmodel.instanceLock
import androidx.lifecycle.ViewModel

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.service.DateTimeService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class SessionListItemViewModel(dateTimeService: DateTimeService, item: ScheduleItem, val selected: () -> Unit) : ViewModel() {
    val title: String = item.session.title
    val isServiceSession: Boolean = item.session.isServiceSession
    val isAttending: Boolean = item.session.rsvp.isAttending
    val isInConflict: Boolean = item.isInConflict
    val speakers: String = item.speakers.joinToString { it.fullName }
    val room: String? = item.room?.name

    val isInPast: Boolean by collected(
        dateTimeService.now() > item.session.endsAt,
        flow {
            while (true) {
                val isInPast = dateTimeService.now() > item.session.endsAt
                emit(isInPast)
                delay(10_000)
            }
        },
    )
    val observeIsInPast by observe(::isInPast)

    class Factory(private val dateTimeService: DateTimeService) {
        fun create(item: ScheduleItem, selected: () -> Unit) = SessionListItemViewModel(dateTimeService, item, selected)
    }
}
