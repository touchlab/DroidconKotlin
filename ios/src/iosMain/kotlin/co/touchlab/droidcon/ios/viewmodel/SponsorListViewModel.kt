package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.gateway.SponsorGateway
import kotlinx.coroutines.flow.flow
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class SponsorListViewModel(
    private val sponsorGateway: SponsorGateway,
    private val sponsorGroupFactory: SponsorGroupViewModel.Factory,
): BaseViewModel() {
    val sponsorGroups: List<SponsorGroupViewModel> by managedList(emptyList(), flow {
        emit(
            sponsorGateway.getSponsors()
                .sortedBy { it.displayPriority }
                .map { sponsorGroup ->
                    sponsorGroupFactory.create(sponsorGroup, onSponsorSelected = { sponsor ->
                        if (sponsor.sponsorId == null) {
                            UIApplication.sharedApplication.openURL(NSURL(string = sponsor.url.string))
                        } else {
                            // TODO: Present detail.
                        }
                    })
                }
        )
    })

    // var presentedSponsorDetail: SponsorDetailViewModel?

    class Factory(
        private val sponsorGateway: SponsorGateway,
        private val sponsorGroupFactory: SponsorGroupViewModel.Factory,
    ) {
        fun create() = SponsorListViewModel(sponsorGateway, sponsorGroupFactory)
    }
}

