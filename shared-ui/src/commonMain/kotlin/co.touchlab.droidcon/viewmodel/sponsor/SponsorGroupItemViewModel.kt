package co.touchlab.droidcon.viewmodel.sponsor
import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.domain.entity.Sponsor
import io.ktor.http.URLParserException
import io.ktor.http.Url

class SponsorGroupItemViewModel(private val sponsor: Sponsor, val selected: () -> Unit) : ViewModel() {

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
