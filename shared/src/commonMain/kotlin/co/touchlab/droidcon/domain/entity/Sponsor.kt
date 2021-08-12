package co.touchlab.droidcon.domain.entity

import co.touchlab.droidcon.composite.Url

class Sponsor(
    override val id: Id,
    val hasDetail: Boolean,
    val description: String?,
    val icon: Url,
    val url: Url,
): DomainEntity<Sponsor.Id>() {

    val name: String
        get() = id.name

    data class Id(val name: String, val group: String)
}
