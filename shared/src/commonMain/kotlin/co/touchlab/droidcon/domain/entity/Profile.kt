package co.touchlab.droidcon.domain.entity

import co.touchlab.droidcon.composite.Url

// TODO: If sponsors are added,
class Profile(
    override val id: Id,
    val fullName: String,
    val bio: String?,
    val tagLine: String?,
    val profilePicture: Url?,
    val twitter: Url?,
    val linkedIn: Url?,
    val website: Url?,
): DomainEntity<Profile.Id>() {
    data class Id(val value: String)
}