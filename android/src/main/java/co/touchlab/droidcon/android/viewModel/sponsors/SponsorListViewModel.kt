package co.touchlab.droidcon.android.viewModel.sponsors

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SponsorListViewModel: ViewModel(), KoinComponent {

    private val sponsorGateway by inject<SponsorGateway>()

    val sponsorGroups: Flow<List<SponsorGroupViewModel>> = flow {
        emit(
            sponsorGateway.getSponsors()
                .sortedBy { it.displayPriority }
                .map(::SponsorGroupViewModel)
        )
    }
}