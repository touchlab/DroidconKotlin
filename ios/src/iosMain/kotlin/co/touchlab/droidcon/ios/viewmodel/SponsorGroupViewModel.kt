package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.entity.SponsorGroup
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SponsorGroupViewModel(
    sponsorGroupItemFactory: SponsorGroupItemViewModel.Factory,
    sponsorGroup: SponsorGroup,
    onSponsorSelected: (Sponsor) -> Unit
): BaseViewModel() {
    val title = sponsorGroup.name
    val sponsors by managedList(
        sponsorGroup.sponsors.map { sponsor ->
            sponsorGroupItemFactory.create(sponsor, selected = { onSponsorSelected(sponsor) })
        }
    )

    class Factory(
        private val sponsorGroupItemFactory: SponsorGroupItemViewModel.Factory,
    ) {
        fun create(
            sponsorGroup: SponsorGroup,
            onSponsorSelected: (Sponsor) -> Unit,
        ) = SponsorGroupViewModel(sponsorGroupItemFactory, sponsorGroup, onSponsorSelected)
    }
}