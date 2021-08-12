package co.touchlab.droidcon.android.viewModel.sponsors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.android.viewModel.sessions.ProfileViewModel
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SponsorDetailViewModel: ViewModel(), KoinComponent {

    private val sponsorGateway by inject<SponsorGateway>()

    var id = MutableStateFlow<Sponsor.Id?>(null)

    private val sponsor = MutableStateFlow<Sponsor?>(
        null
    )

    val name: Flow<String> = sponsor.map { it?.name ?: "" }

    // TODO: Get from entity.
    val groupTitle: Flow<String> = flowOf("Gold")
    val imageUrl: Flow<Url?> = sponsor.map { it?.icon }
    val webUrl: Flow<Url?> = sponsor.map { it?.url }

    // TODO: Get from entity.
    val description: Flow<String> =
        flowOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed mauris orci, bibendum ac bibendum eget, fermentum vel ante. Proin elementum lacus sem, at tempor orci ultrices a. Quisque quis dignissim lorem. Nulla facilisi. Curabitur vel commodo ex. Duis lobortis interdum mauris, sit amet cursus mi volutpat vitae.")

    // TODO: Get from entity.
    val representative: Flow<ProfileViewModel> = flowOf(
        ProfileViewModel(
            Profile(
                id = Profile.Id("representative"),
                fullName = "John Doe",
                bio = "Aliquam sit amet purus sit amet neque semper rhoncus. Maecenas scelerisque tellus commodo, pretium turpis at, lacinia neque. Nullam ac cursus lacus, et interdum ex. Aliquam arcu sem, ullamcorper sit amet purus non, rhoncus bibendum eros.",
                tagLine = "Android developer",
                profilePicture = null,
                twitter = null,
                linkedIn = null,
                website = null,
            )
        )
    )

    init {
        viewModelScope.launch {
            // TODO: Get sponsor for the id, set it to sponsor state flow.
        }
    }
}