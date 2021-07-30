package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Profile
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