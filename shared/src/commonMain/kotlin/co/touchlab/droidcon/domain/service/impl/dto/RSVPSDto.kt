package co.touchlab.droidcon.domain.service.impl.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


object RSVPSDto {

    @Serializable
    data class RSVPsCollectionDto(
        @SerialName("documents")
        val groups: SponsorGroupDto,
    )

    @Serializable
    data class SponsorGroupDto(
        val sessions: List<String>,
    )
}
