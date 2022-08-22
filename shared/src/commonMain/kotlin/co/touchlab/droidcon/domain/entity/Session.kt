package co.touchlab.droidcon.domain.entity

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.parcelable.WriteWith
import kotlinx.datetime.Instant

@Parcelize
class Session(
    override val id: Id,
    val title: String,
    val description: String?,
    val startsAt: @WriteWith<InstantParceler> Instant,
    val endsAt: @WriteWith<InstantParceler> Instant,
    val isServiceSession: Boolean,
    val room: Room.Id?,
    var rsvp: RSVP,
    var feedback: Feedback?,
): DomainEntity<Session.Id>(), Parcelable {

    @Parcelize
    data class Id(val value: String): Parcelable

    @Parcelize
    data class RSVP(
        val isAttending: Boolean,
        val isSent: Boolean,
    ): Parcelable

    @Parcelize
    data class Feedback(
        val rating: Int,
        val comment: String,
        val isSent: Boolean,
    ): Parcelable {

        object Rating {

            const val DISSATISFIED = 1
            const val NORMAL = 2
            const val SATISFIED = 3
        }
    }
}
