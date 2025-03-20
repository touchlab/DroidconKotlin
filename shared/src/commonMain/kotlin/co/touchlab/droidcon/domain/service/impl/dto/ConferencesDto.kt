package co.touchlab.droidcon.domain.service.impl.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object ConferencesDto {

    @Serializable
    data class ConferenceCollectionDto(
        @SerialName("documents")
        val conferences: List<ConferenceDocumentDto>,
    )

    @Serializable
    data class ConferenceDocumentDto(val name: String, val fields: ConferenceFields, val createTime: String, val updateTime: String)

    @Serializable
    data class ConferenceFields(
        val conferenceName: StringValue,
        val conferenceTimeZone: StringValue,
        val projectId: StringValue,
        val collectionName: StringValue,
        val apiKey: StringValue,
        val scheduleId: StringValue,
    )

    @Serializable
    data class StringValue(val stringValue: String)
}
