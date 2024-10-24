package co.touchlab.droidcon.domain.entity

class SponsorGroup(override val id: Id, val displayPriority: Int, val isProminent: Boolean) : DomainEntity<SponsorGroup.Id>() {
    val name: String
        get() = id.value

    data class Id(val value: String)
}
