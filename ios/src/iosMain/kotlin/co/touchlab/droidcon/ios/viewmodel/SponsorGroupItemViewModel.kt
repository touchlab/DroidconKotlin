package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.entity.Sponsor
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SponsorGroupItemViewModel(
    private val sponsor: Sponsor,
    val selected: () -> Unit,
): BaseViewModel() {
    val name = sponsor.name
    val imageUrl = sponsor.icon

    class Factory {
        fun create(sponsor: Sponsor, selected: () -> Unit) =
            SponsorGroupItemViewModel(sponsor, selected)
    }
}