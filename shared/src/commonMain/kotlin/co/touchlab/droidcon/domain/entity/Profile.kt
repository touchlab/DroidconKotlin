package co.touchlab.droidcon.domain.entity

import co.touchlab.droidcon.composite.Url
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

// TODO: Add sponsors if desired.
@Parcelize
class Profile(
    override val id: Id,
    val fullName: String,
    val bio: String?,
    val tagLine: String?,
    val profilePicture: Url?,
    val twitter: Url?,
    val linkedIn: Url?,
    val website: Url?,
): DomainEntity<Profile.Id>(), Parcelable {

    @Parcelize
    data class Id(val value: String): Parcelable
}
