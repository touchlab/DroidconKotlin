package co.touchlab.droidcon.domain.composite

import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.entity.SponsorGroup

data class SponsorGroupWithSponsors(val group: SponsorGroup, val sponsors: List<Sponsor>)
