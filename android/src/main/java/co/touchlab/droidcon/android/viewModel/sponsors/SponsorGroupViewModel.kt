package co.touchlab.droidcon.android.viewModel.sponsors

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.domain.entity.SponsorGroup
import kotlinx.coroutines.flow.MutableStateFlow

class SponsorGroupViewModel(
    sponsorGroup: SponsorGroup,
): ViewModel() {

    val title: String = sponsorGroup.name
    val sponsors: MutableStateFlow<List<SponsorGroupItemViewModel>> = MutableStateFlow(
        sponsorGroup.sponsors.map { sponsor ->
            SponsorGroupItemViewModel(sponsor)
        }
    )
    val isProminent: Boolean = sponsorGroup.isProminent
}