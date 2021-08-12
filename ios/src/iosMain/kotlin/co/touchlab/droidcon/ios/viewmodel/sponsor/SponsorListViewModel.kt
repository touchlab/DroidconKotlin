package co.touchlab.droidcon.ios.viewmodel.sponsor

import co.touchlab.droidcon.domain.gateway.SponsorGateway
import kotlinx.coroutines.flow.flow
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class SponsorListViewModel(
    private val sponsorGateway: SponsorGateway,
    private val sponsorGroupFactory: SponsorGroupViewModel.Factory,
    private val sponsorDetailFactory: SponsorDetailViewModel.Factory,
): BaseViewModel() {
    val sponsorGroups: List<SponsorGroupViewModel> by managedList(emptyList(), flow {
        emit(
            sponsorGateway.getSponsors()
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
        )
    })

    var presentedSponsorDetail: SponsorDetailViewModel? by managed(null)

    class Factory(
        private val sponsorGateway: SponsorGateway,
        private val sponsorGroupFactory: SponsorGroupViewModel.Factory,
        private val sponsorDetailFactory: SponsorDetailViewModel.Factory,
    ) {
        fun create() = SponsorListViewModel(sponsorGateway, sponsorGroupFactory, sponsorDetailFactory)
    }
}