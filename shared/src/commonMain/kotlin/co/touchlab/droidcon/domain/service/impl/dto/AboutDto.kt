package co.touchlab.droidcon.domain.service.impl.dto

import kotlinx.serialization.Serializable

object AboutDto {

    @Serializable
    data class AboutItemDto(
        val icon: String,
        val title: String,
        val detail: String,
    )
}
