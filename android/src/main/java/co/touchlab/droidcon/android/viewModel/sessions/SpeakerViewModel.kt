package co.touchlab.droidcon.android.viewModel.sessions

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.android.dto.WebLink
import co.touchlab.droidcon.android.service.ParseUrlViewService
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Profile
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpeakerViewModel(profile: Profile): ViewModel(), KoinComponent {

    private val parseUrlViewService by inject<ParseUrlViewService>()

    val id: Profile.Id = profile.id

    val imageUrl: Url? = profile.profilePicture

    val name: String = profile.fullName
    val title: String = name + (profile.tagLine?.let { ", $it" } ?: "")

    val bio: Pair<String, List<WebLink>>? = profile.bio?.let { it to parseUrlViewService.parse(it) }
}