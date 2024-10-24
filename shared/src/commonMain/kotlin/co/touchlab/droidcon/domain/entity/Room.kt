package co.touchlab.droidcon.domain.entity

class Room(override val id: Id, val name: String) : DomainEntity<Room.Id>() {
    data class Id(val value: Long)
}
