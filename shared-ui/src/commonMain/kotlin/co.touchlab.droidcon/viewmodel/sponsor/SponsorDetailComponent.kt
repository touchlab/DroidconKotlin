package co.touchlab.droidcon.viewmodel.sponsor

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.decompose.whileStarted
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import co.touchlab.droidcon.util.DcDispatchers
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce

// TODO: Connect to a gateway.
class SponsorDetailComponent(
    componentContext: ComponentContext,
    dispatchers: DcDispatchers,
    private val sponsorGateway: SponsorGateway,
    private val sponsor: Sponsor,
    private val speakerSelected: (Profile) -> Unit,
    private val backPressed: () -> Unit,
): ComponentContext by componentContext {

    private val _model =
        MutableValue(
            Model(
                name = sponsor.name,
                groupName = sponsor.group,
                imageUrl = sponsor.icon,
                abstract = sponsor.description,
            )
        )

    val model: Value<Model> get() = _model

    init {
        whileStarted(dispatchers.main) {
            val representatives =
                sponsorGateway
                    .getRepresentatives(sponsor.id)
                    .map { profile ->
                        Model.Representative(
                            profile = profile,
                            avatarUrl = profile.profilePicture,
                            info = listOfNotNull(profile.fullName, profile.tagLine).joinToString(),
                            bio = profile.bio,
                        )
                    }

            _model.reduce { it.copy(representatives = representatives) }
        }
    }

    fun backTapped() {
        backPressed()
    }

    fun representativeTapped(representative: Model.Representative) {
        speakerSelected(representative.profile)
    }

    data class Model(
        val name: String,
        val groupName: String,
        val imageUrl: Url,
        val abstract: String?,
        val representatives: List<Representative> = emptyList(),
    ) {

        data class Representative(
            val profile: Profile,
            val avatarUrl: Url?,
            val info: String,
            val bio: String?,
        )
    }

    class Factory(
        private val dispatchers: DcDispatchers,
        private val sponsorGateway: SponsorGateway,
    ) {

        fun create(componentContext: ComponentContext, sponsor: Sponsor, speakerSelected: (Profile) -> Unit, backPressed: () -> Unit) =
            SponsorDetailComponent(
                componentContext,
                dispatchers,
                sponsorGateway,
                sponsor,
                speakerSelected,
                backPressed,
            )
    }
}
