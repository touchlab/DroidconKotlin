package co.touchlab.sessionize.db

import co.touchlab.droidcon.db.Sponsor

data class SponsorGroupDbItem(val groupName: String, val sponsors: List<Sponsor>)