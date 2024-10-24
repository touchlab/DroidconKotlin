package co.touchlab.droidcon.viewmodel.sponsor

import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import co.touchlab.droidcon.viewmodel.session.SpeakerDetailViewModel
import co.touchlab.droidcon.viewmodel.session.SpeakerListItemViewModel
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

// TODO: Connect to a gateway.
class SponsorDetailViewModel(
    private val sponsorGateway: SponsorGateway,
    private val speakerListItemFactory: SpeakerListItemViewModel.Factory,
    private val speakerDetailFactory: SpeakerDetailViewModel.Factory,
    private val sponsor: Sponsor,
    val groupName: String,
) : BaseViewModel() {

    val name = sponsor.name
    val imageUrl = sponsor.icon

    val abstract = sponsor.description

    val representatives: List<SpeakerListItemViewModel> by managedList(emptyList())
    val observeRepresentatives by observe(::representatives)

    var presentedSpeakerDetail: SpeakerDetailViewModel? by managed(null)
    val observePresentedSpeakerDetail by observe(::presentedSpeakerDetail)

    override suspend fun whileAttached() {
        sponsorGateway.getRepresentatives(sponsor.id).map { speaker ->
            speakerListItemFactory.create(
                speaker,
                selected = {
                    presentedSpeakerDetail = speakerDetailFactory.create(speaker)
                },
            )
        }
    }

    class Factory(
        private val sponsorGateway: SponsorGateway,
        private val speakerListItemFactory: SpeakerListItemViewModel.Factory,
        private val speakerDetailFactory: SpeakerDetailViewModel.Factory,
    ) {

        fun create(sponsor: Sponsor, groupName: String) =
            SponsorDetailViewModel(sponsorGateway, speakerListItemFactory, speakerDetailFactory, sponsor, groupName)
    }
}
