package co.touchlab.droidcon.ios.viewmodel.sponsor

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.ios.viewmodel.session.SpeakerDetailViewModel
import co.touchlab.droidcon.ios.viewmodel.session.SpeakerListItemViewModel
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import org.brightify.hyperdrive.multiplatformx.property.map

// TODO: Connect to a gateway.
class SponsorDetailViewModel(
    private val speakerListItemFactory: SpeakerListItemViewModel.Factory,
    private val speakerDetailFactory: SpeakerDetailViewModel.Factory,
    sponsor: Sponsor,
    val groupName: String,
): BaseViewModel() {
    val name = sponsor.name
    val imageUrl = sponsor.icon

    val abstract = "We are very hard-working here https://stackoverflow.com/, but we also know when it's time to kick back and relax with an AriZona can in hand. https://stackoverflow.com/questions/995219/how-to-make-uitextview-detect-links-for-website-mail-and-phone-number"

    val speakers: List<SpeakerListItemViewModel> by managedList(
        // observeItem.map {
            listOf(Profile(Profile.Id(""), "Speakerino", null, null, null, null, null, null)).map { speaker ->
                speakerListItemFactory.create(speaker, selected = {
                    presentedSpeakerDetail = speakerDetailFactory.create(speaker)
                })
            }
        // }
    )

    var presentedSpeakerDetail: SpeakerDetailViewModel? by managed(null)

    class Factory(
        private val speakerListItemFactory: SpeakerListItemViewModel.Factory,
        private val speakerDetailFactory: SpeakerDetailViewModel.Factory,
    ) {
        fun create(sponsor: Sponsor, groupName: String) =
            SponsorDetailViewModel(speakerListItemFactory, speakerDetailFactory, sponsor, groupName)
    }
}