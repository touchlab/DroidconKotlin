package co.touchlab.droidcon.domain.entity

import co.touchlab.droidcon.composite.Url
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
class Sponsor(
    override val id: Id,
    val hasDetail: Boolean,
    val description: String?,
    val icon: Url,
    val url: Url,
): DomainEntity<Sponsor.Id>(), Parcelable {

    val name: String
        get() = id.name

    val group: String
        get() = id.group

    @Parcelize
    data class Id(val name: String, val group: String): Parcelable
}
