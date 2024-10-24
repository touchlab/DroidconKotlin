package co.touchlab.droidcon.viewmodel.sponsor

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import kotlinx.coroutines.flow.map
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SponsorListViewModel(
    private val sponsorGateway: SponsorGateway,
    private val sponsorGroupFactory: SponsorGroupViewModel.Factory,
    private val sponsorDetailFactory: SponsorDetailViewModel.Factory,
) : BaseViewModel() {
    val sponsorGroups: List<SponsorGroupViewModel> by managedList(
        emptyList(),
        sponsorGateway.observeSponsors()
            .map { sponsorGroups ->
                sponsorGroups
                    .sortedBy { it.group.displayPriority }
                    .map { sponsorGroup ->
                        sponsorGroupFactory.create(
                            sponsorGroup,
                            onSponsorSelected = { sponsor ->
                                if (sponsor.hasDetail) {
                                    presentedSponsorDetail = sponsorDetailFactory.create(
                                        sponsor,
                                        sponsorGroup.group.name
                                    )
                                } else {
                                    // UIApplication.sharedApplication.openURL(NSURL(string = sponsor.url.string))
                                    presentedUrl = sponsor.url
                                }
                            }
                        )
                    }
            }
    )
    val observeSponsorGroups by observe(::sponsorGroups)

    var presentedSponsorDetail: SponsorDetailViewModel? by managed(null)
    val observePresentedSponsorDetail by observe(::presentedSponsorDetail)

    var presentedUrl: Url? by published(null)
    val observePresentedUrl by observe(::presentedUrl)

    class Factory(
        private val sponsorGateway: SponsorGateway,
        private val sponsorGroupFactory: SponsorGroupViewModel.Factory,
        private val sponsorDetailFactory: SponsorDetailViewModel.Factory,
    ) {

        fun create() =
            SponsorListViewModel(sponsorGateway, sponsorGroupFactory, sponsorDetailFactory)
    }
}
