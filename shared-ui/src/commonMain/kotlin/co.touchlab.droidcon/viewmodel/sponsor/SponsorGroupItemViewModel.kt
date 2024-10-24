package co.touchlab.droidcon.viewmodel.sponsor

import co.touchlab.droidcon.domain.entity.Sponsor
import io.ktor.http.URLParserException
import io.ktor.http.Url
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SponsorGroupItemViewModel(private val sponsor: Sponsor, val selected: () -> Unit) : BaseViewModel() {

    val name = sponsor.name
    val imageUrl = sponsor.icon

    val validImageUrl: String? =
        try {
            Url(sponsor.icon.string).toString()
        } catch (e: URLParserException) {
            null
        }

    class Factory {

        fun create(sponsor: Sponsor, selected: () -> Unit) = SponsorGroupItemViewModel(sponsor, selected)
    }
}
