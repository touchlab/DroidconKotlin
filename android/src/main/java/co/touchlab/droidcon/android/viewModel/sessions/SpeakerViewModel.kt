package co.touchlab.droidcon.android.viewModel.sessions

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Profile

class SpeakerViewModel(profile: Profile): ViewModel() {

    val imageUrl: Url? = profile.profilePicture

    val name: String = profile.fullName
    val title: String = name + (profile.tagLine?.let { ", $it" } ?: "")

    val bio: String? = profile.bio
}