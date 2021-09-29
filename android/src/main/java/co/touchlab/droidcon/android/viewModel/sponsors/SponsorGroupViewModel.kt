package co.touchlab.droidcon.android.viewModel.sponsors

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.SponsorGroup
import kotlinx.coroutines.flow.MutableStateFlow

class SponsorGroupViewModel(
    sponsorGroup: SponsorGroupWithSponsors,
): ViewModel() {

    val title: String = sponsorGroup.group.name
    val sponsors: MutableStateFlow<List<SponsorGroupItemViewModel>> = MutableStateFlow(
        sponsorGroup.sponsors.map { sponsor ->
            SponsorGroupItemViewModel(sponsor)
        }
    )
    val isProminent: Boolean = sponsorGroup.group.isProminent
}