package co.touchlab.droidcon.android.viewModel.sessions

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.dto.WebLink
import co.touchlab.droidcon.android.service.ParseUrlViewService
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpeakerDetailViewModel: ViewModel(), KoinComponent {

    // private val speakerRepository by inject<SpeakerRepository>()
    private val parseUrlViewService by inject<ParseUrlViewService>()

    var id = MutableStateFlow<Profile.Id?>(null)

    private val speaker = MutableStateFlow<Profile?>(
        // TODO: Remove mock data when speaker repository is implemented.
        Profile(
            id = Profile.Id("juli"),
            fullName = "Juli a Tabi",
            bio = "Mauris venenatis tempus magna et accumsan. Morbi mi urna, rutrum in urna in, rhoncus elementum elit. Nam ornare suscipit dolor, ut elementum nisi tempor ut.",
            tagLine = "Android developer",
            profilePicture = Url("https://juliajakubcova.com/img/image-01.jpg"),
            twitter = null,
            linkedIn = null,
            website = Url("https://juliajakubcova.com"),
        )
    )

    val name: Flow<String> = speaker.map { it?.fullName ?: "" }
    val tagLine: Flow<String> = speaker.map { it?.tagLine ?: "" }
    val imageUrl: Flow<Url?> = speaker.map { it?.profilePicture }

    val infoList: Flow<List<SpeakerInfo>> = speaker.map { profile ->
        if (profile == null) return@map emptyList()

        mutableListOf<SpeakerInfo>().apply {
            profile.tagLine?.let {
                add(
                    SpeakerInfo(
                        text = it,
                        links = parseUrlViewService.parse(it),
                        iconRes = R.drawable.icon_company,
                    )
                )
            }

            profile.website?.let {
                add(
                    SpeakerInfo(
                        text = it.string,
                        links = parseUrlViewService.parse(it.string),
                        iconRes = R.drawable.icon_website,
                    )
                )
            }

            profile.twitter?.let {
                add(
                    SpeakerInfo(
                        text = it.string,
                        links = parseUrlViewService.parse(it.string),
                        iconRes = R.drawable.icon_twitter,
                    )
                )
            }

            profile.linkedIn?.let {
                add(
                    SpeakerInfo(
                        text = it.string,
                        links = parseUrlViewService.parse(it.string),
                        iconRes = R.drawable.icon_linkedin,
                    )
                )
            }

            profile.bio?.let {
                add(
                    SpeakerInfo(
                        text = it,
                        links = parseUrlViewService.parse(it),
                        iconRes = R.drawable.icon_profile,
                    )
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            // TODO: Get profile for the id, set it to speaker state flow.
        }
    }

    data class SpeakerInfo(val text: String, val links: List<WebLink>, @DrawableRes val iconRes: Int)
}