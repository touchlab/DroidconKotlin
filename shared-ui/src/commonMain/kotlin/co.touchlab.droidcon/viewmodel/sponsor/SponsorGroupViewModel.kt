package co.touchlab.droidcon.viewmodel.sponsor

import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.viewmodel.ViewModelFactory
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SponsorGroupViewModel(
    sponsorGroupItemFactory: ViewModelFactory.SponsorGroupItemViewModelFactory,
    sponsorGroup: SponsorGroupWithSponsors,
    onSponsorSelected: (Sponsor) -> Unit,
) : BaseViewModel() {
    val title = sponsorGroup.group.name
    val isProminent = sponsorGroup.group.isProminent
    val sponsors by managedList(
        sponsorGroup.sponsors.map { sponsor ->
            sponsorGroupItemFactory.create(sponsor, selected = { onSponsorSelected(sponsor) })
        },
    )
    val observeSponsors by observe(::sponsors)
}
