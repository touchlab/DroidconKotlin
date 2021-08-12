package co.touchlab.droidcon.android.viewModel.sponsors

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Sponsor

class SponsorGroupItemViewModel(
    sponsor: Sponsor,
): ViewModel() {

    val id: Sponsor.Id = sponsor.id
    val name: String = sponsor.name
    val imageUrl: Url = sponsor.icon
    val url: Url = sponsor.url
}