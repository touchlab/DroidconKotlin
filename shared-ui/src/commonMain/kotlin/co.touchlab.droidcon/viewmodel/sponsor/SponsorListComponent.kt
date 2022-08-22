package co.touchlab.droidcon.viewmodel.sponsor

import co.touchlab.droidcon.decompose.whileStarted
import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import co.touchlab.droidcon.util.DcDispatchers
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import io.ktor.http.URLParserException
import kotlinx.coroutines.flow.map

class SponsorListComponent(
    private val componentContext: ComponentContext,
    dispatchers: DcDispatchers,
    private val sponsorGateway: SponsorGateway,
    private val sponsorSelected: (Sponsor) -> Unit,
): ComponentContext by componentContext {

    private val _model = MutableValue(Model())
    val model: Value<Model> get() = _model

    init {
        whileStarted(dispatchers.main) {
            sponsorGateway.observeSponsors()
                .map { groups ->
                    groups
                        .sortedBy { it.group.displayPriority }
                        .map { it.toModel() }
                }
                .collect { groups ->
                    _model.reduce { it.copy(groups = groups) }
                }
        }
    }

    private fun SponsorGroupWithSponsors.toModel(): Model.Group =
        Model.Group(
            title = group.name,
            isProminent = group.isProminent,
            sponsors = sponsors.map { it.toModel() },
        )

    private fun Sponsor.toModel(): Model.Sponsor =
        Model.Sponsor(
            sponsor = this,
            name = name,
            imageUrl = try {
                io.ktor.http.Url(icon.string).toString()
            } catch (e: URLParserException) {
                null
            },
        )

    fun sponsorTapped(sponsor: Model.Sponsor) {
        sponsorSelected(sponsor.sponsor)
    }

    data class Model(
        val groups: List<Group> = emptyList(),
    ) {

        data class Group(
            val title: String,
            val isProminent: Boolean,
            val sponsors: List<Sponsor>,
        )

        data class Sponsor(
            val sponsor: co.touchlab.droidcon.domain.entity.Sponsor,
            val name: String,
            val imageUrl: String?,
        )
    }

    class Factory(
        private val dispatchers: DcDispatchers,
        private val sponsorGateway: SponsorGateway,
    ) {

        fun create(componentContext: ComponentContext, sponsorSelected: (Sponsor) -> Unit) =
            SponsorListComponent(componentContext, dispatchers, sponsorGateway, sponsorSelected)
    }
}
