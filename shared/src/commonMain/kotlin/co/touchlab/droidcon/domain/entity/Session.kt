package co.touchlab.droidcon.domain.entity

import co.touchlab.droidcon.domain.service.DateTimeService
import kotlinx.datetime.Instant

class Session(
    private val dateTimeService: DateTimeService,
    override val id: Id,
    val title: String,
    val description: String?,
    val startsAt: Instant,
    val endsAt: Instant,
    val isServiceSession: Boolean,
    val room: Room.Id?,
    var rsvp: RSVP,
    var feedback: Feedback?,
) : DomainEntity<Session.Id>() {

    val isAttendable: Boolean
        get() = dateTimeService.now() < endsAt

    data class Id(val value: String)

    data class RSVP(val isAttending: Boolean, val isSent: Boolean)

    data class Feedback(val rating: Int, val comment: String, val isSent: Boolean) {
        object Rating {
            const val DISSATISFIED = 1
            const val NORMAL = 2
            const val SATISFIED = 3
        }
    }
}
