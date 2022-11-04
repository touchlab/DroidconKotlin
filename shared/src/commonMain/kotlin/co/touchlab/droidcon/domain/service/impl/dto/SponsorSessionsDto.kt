package co.touchlab.droidcon.domain.service.impl.dto

import kotlinx.serialization.Serializable

object SponsorSessionsDto {

    @Serializable
    data class SessionGroupDto(
        val sessions: List<SessionDto>
    )

    @Serializable
    data class SessionDto(
        val id: String,
        val title: String,
        val description: String?,
        val speakers: List<SpeakerReferenceDto>,
    )

    @Serializable
    data class SpeakerReferenceDto(
        val id: String,
        val name: String,
    )
}
