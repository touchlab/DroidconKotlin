package co.touchlab.droidcon.ios.viewmodel.session

import co.touchlab.droidcon.domain.entity.Profile
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SpeakerListItemViewModel(profile: Profile, val selected: () -> Unit): BaseViewModel() {
    val avatarUrl = profile.profilePicture
    val info = listOfNotNull(profile.fullName, profile.tagLine).joinToString()
    val bio = profile.bio

    class Factory {
        fun create(profile: Profile, selected: () -> Unit) = SpeakerListItemViewModel(profile, selected)
    }
}