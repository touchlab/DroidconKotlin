package co.touchlab.droidcon.viewmodel.sponsor

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.viewmodel.managedList
import co.touchlab.droidcon.viewmodel.observe

class SponsorGroupViewModel(
    sponsorGroupItemFactory: SponsorGroupItemViewModel.Factory,
    sponsorGroup: SponsorGroupWithSponsors,
    onSponsorSelected: (Sponsor) -> Unit,
) : ViewModel() {
    val title = sponsorGroup.group.name
    val isProminent = sponsorGroup.group.isProminent
    val sponsors by managedList(
        sponsorGroup.sponsors.map { sponsor ->
            sponsorGroupItemFactory.create(sponsor, selected = { onSponsorSelected(sponsor) })
        },
    )
    val observeSponsors by observe(::sponsors)

    class Factory(private val sponsorGroupItemFactory: SponsorGroupItemViewModel.Factory) {
        fun create(sponsorGroup: SponsorGroupWithSponsors, onSponsorSelected: (Sponsor) -> Unit) =
            SponsorGroupViewModel(sponsorGroupItemFactory, sponsorGroup, onSponsorSelected)
    }
}
