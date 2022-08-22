package co.touchlab.droidcon.domain.entity

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

class Room(
    override val id: Id,
    val name: String,
): DomainEntity<Room.Id>() {

    @Parcelize
    data class Id(val value: Long): Parcelable
}
