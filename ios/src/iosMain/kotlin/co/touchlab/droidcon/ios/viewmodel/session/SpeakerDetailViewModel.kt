package co.touchlab.droidcon.ios.viewmodel.session

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.ios.viewmodel.settings.WebLink
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SpeakerDetailViewModel(
    profile: Profile,
): BaseViewModel() {

    val avatarUrl = profile.profilePicture

    val name = profile.fullName
    val position = profile.tagLine

    val socials = Socials(
        website = profile.website,
        twitter = profile.twitter,
        linkedIn = profile.linkedIn,
    )

    val bio = profile.bio
    val bioWebLinks: List<WebLink> = bio?.let(::parseUrl) ?: emptyList()

    private fun parseUrl(text: String): List<WebLink> {
        val urlRegex =
            "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)".toRegex()
        return urlRegex.findAll(text).map { WebLink(it.range, it.value) }.toList()
    }

    data class Socials(
        val website: Url?,
        val twitter: Url?,
        val linkedIn: Url?,
    ) {

        val isEmpty: Boolean = listOfNotNull(
            website,
            twitter,
            linkedIn,
        ).isEmpty()
    }

    class Factory {
        fun create(profile: Profile) = SpeakerDetailViewModel(profile)
    }
}
