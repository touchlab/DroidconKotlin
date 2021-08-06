package co.touchlab.droidcon.domain.service.impl.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

object SponsorsDto {

    @Serializable
    data class SponsorCollectionDto(
        @SerialName("documents")
        val groups: List<SponsorGroupDto>,
    )

    @Serializable
    data class SponsorGroupDto(
        val name: String,
        val fields: DocumentFields,
        val createTime: String,
        val updateTime: String,
    )

    @Serializable
    data class DocumentFields(
        val displayOrder: DisplayOrder,
        val sponsors: Sponsors,
    )

    @Serializable
    data class DisplayOrder(
        val integerValue: String,
    )

    @Serializable
    data class Sponsors(
        val arrayValue: ArrayValue,
    )

    @Serializable
    data class ArrayValue(
        val values: List<Value>,
    )

    @Serializable
    data class Value(
        val mapValue: MapValue,
    )

    @Serializable
    data class MapValue(
        val fields: MapValueFields,
    )

    @Serializable
    data class MapValueFields(
        val sponsorId: StringValue? = null,
        val name: StringValue,
        val icon: StringValue,
        val url: StringValue,
    )

    @Serializable
    data class StringValue(
        val stringValue: String,
    )
}
