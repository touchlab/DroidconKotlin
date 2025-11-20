package co.touchlab.droidcon.viewmodel.session
import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.domain.entity.Profile

class SpeakerListItemViewModel(profile: Profile, val selected: () -> Unit) : ViewModel() {
    val avatarUrl = profile.profilePicture
    val info = listOfNotNull(profile.fullName, profile.tagLine).joinToString()
    val bio = profile.bio

    class Factory {
        fun create(profile: Profile, selected: () -> Unit) = SpeakerListItemViewModel(profile, selected)
    }
}
