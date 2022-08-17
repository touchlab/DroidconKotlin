package co.touchlab.droidcon.android.viewModel.sessions

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.R
import co.touchlab.droidcon.dto.WebLink
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalCoroutinesApi::class)
class SpeakerDetailViewModel: ViewModel(), KoinComponent {

    private val profileRepository by inject<ProfileRepository>()
    private val parseUrlViewService by inject<ParseUrlViewService>()

    var id = MutableStateFlow<Profile.Id?>(null)

    private val speaker = MutableStateFlow<Profile?>(null)

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
                        iconRes = R.drawable.twitter,
                    )
                )
            }

            profile.linkedIn?.let {
                add(
                    SpeakerInfo(
                        text = it.string,
                        links = parseUrlViewService.parse(it.string),
                        iconRes = R.drawable.linkedin,
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
            id.flatMapLatest { profileId ->
                profileId?.let { profileRepository.observe(it) } ?: flowOf(null)
            }.collect {
                speaker.value = it
            }
        }
    }

    data class SpeakerInfo(val text: String, val links: List<WebLink>, @DrawableRes val iconRes: Int)
}
