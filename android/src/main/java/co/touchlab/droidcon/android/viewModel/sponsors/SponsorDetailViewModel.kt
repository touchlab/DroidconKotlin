package co.touchlab.droidcon.android.viewModel.sponsors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.android.viewModel.sessions.ProfileViewModel
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SponsorDetailViewModel: ViewModel(), KoinComponent {

    private val sponsorGateway by inject<SponsorGateway>()

    var id = MutableStateFlow<Sponsor.Id?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val sponsor: SharedFlow<Sponsor?> =
        id.flatMapLatest { it?.let { sponsorGateway.observeSponsorById(it) } ?: emptyFlow() }.shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    val name: Flow<String> = sponsor.map { it?.name ?: "" }
    val groupTitle: Flow<String> = sponsor.map { it?.group ?: "" }
    val imageUrl: Flow<Url?> = sponsor.map { it?.icon }
    val webUrl: Flow<Url?> = sponsor.map { it?.url }
    val description: Flow<String?> = sponsor.map { it?.description }
    val representatives: SharedFlow<List<ProfileViewModel>> =
        id.map { it?.let { sponsorGateway.getRepresentatives(it) } ?: emptyList() }
            .map { it.map(::ProfileViewModel) }
            .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)
}