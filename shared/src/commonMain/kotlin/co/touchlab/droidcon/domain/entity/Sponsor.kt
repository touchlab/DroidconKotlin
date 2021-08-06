package co.touchlab.droidcon.domain.entity

import co.touchlab.droidcon.composite.Url

data class Sponsor(
    val sponsorId: String?,
    val name: String,
    val icon: Url,
    val url: Url,
)
