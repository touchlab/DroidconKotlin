package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.dto.WebLink
import co.touchlab.droidcon.service.ParseUrlViewService

class SpeakerDetailComponent(
    private val parseUrlViewService: ParseUrlViewService,
    profile: Profile,
    private val backPressed: () -> Unit,
) {

    val avatarUrl = profile.profilePicture

    val name = profile.fullName
    val position = profile.tagLine

    val socials = Socials(
        website = profile.website,
        twitter = profile.twitter,
        linkedIn = profile.linkedIn,
    )

    val bio = profile.bio
    val bioWebLinks: List<WebLink> = bio?.let(parseUrlViewService::parse) ?: emptyList()

    fun backTapped() {
        backPressed()
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

    class Factory(private val parseUrlViewService: ParseUrlViewService) {

        fun create(
            profile: Profile,
            backPressed: () -> Unit,
        ) = SpeakerDetailComponent(parseUrlViewService, profile, backPressed)
    }
}
