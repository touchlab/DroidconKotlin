package co.touchlab.droidcon.ios.viewmodel.sponsor

import co.touchlab.droidcon.domain.gateway.SponsorGateway
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class SponsorListViewModel(
    private val sponsorGateway: SponsorGateway,
    private val sponsorGroupFactory: SponsorGroupViewModel.Factory,
    private val sponsorDetailFactory: SponsorDetailViewModel.Factory,
): BaseViewModel() {
    val sponsorGroups: List<SponsorGroupViewModel> by managedList(emptyList(),
        sponsorGateway.observeSponsors()
            .map { sponsorGroups ->
                sponsorGroups
                    .sortedBy { it.group.displayPriority }
                    .map { sponsorGroup ->
                        sponsorGroupFactory.create(sponsorGroup, onSponsorSelected = { sponsor ->
                            if (sponsor.hasDetail) {
                                presentedSponsorDetail = sponsorDetailFactory.create(sponsor, sponsorGroup.group.name)
                            } else {
                                UIApplication.sharedApplication.openURL(NSURL(string = sponsor.url.string))
                            }
                        })
                    }
            }
    )
    val observeSponsorGroups by observe(::sponsorGroups)

    var presentedSponsorDetail: SponsorDetailViewModel? by managed(null)

    class Factory(
        private val sponsorGateway: SponsorGateway,
        private val sponsorGroupFactory: SponsorGroupViewModel.Factory,
        private val sponsorDetailFactory: SponsorDetailViewModel.Factory,
    ) {
        fun create() = SponsorListViewModel(sponsorGateway, sponsorGroupFactory, sponsorDetailFactory)
    }
}
