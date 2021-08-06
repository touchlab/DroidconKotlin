package co.touchlab.droidcon.domain.entity

data class SponsorGroup(
    val name: String,
    val sponsors: List<Sponsor>,
    val displayPriority: Int,
)