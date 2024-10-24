package co.touchlab.droidcon.domain.service.impl.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray

object SpeakersDto {

    @Serializable
    data class SpeakerDto(
        val id: String,
        val firstName: String,
        val lastName: String,
        val fullName: String,
        val bio: String?,
        val tagLine: String?,
        val profilePicture: String?,
        val sessions: List<SessionDto>,
        val isTopSpeaker: Boolean,
        val links: List<LinkDto>,
        val questionAnswers: JsonArray,
        val categories: JsonArray,
    )

    @Serializable
    data class LinkDto(val title: String, val url: String, val linkType: LinkType)

    @Serializable(with = LinkType.Companion::class)
    data class LinkType(val value: String) {
        companion object : KSerializer<LinkType> {
            val Blog = LinkType("Blog")
            val CompanyWebsite = LinkType("Company_Website")
            val LinkedIn = LinkType("LinkedIn")
            val Other = LinkType("Other")
            val Twitter = LinkType("Twitter")

            private val allTypes = listOf(
                Blog,
                CompanyWebsite,
                LinkedIn,
                Other,
                Twitter,
            ).associateBy { it.value }

            override val descriptor: SerialDescriptor
                get() {
                    return PrimitiveSerialDescriptor("co.touchlab.droidcon.domain.service.impl.dto.LinkType", PrimitiveKind.STRING)
                }

            override fun deserialize(decoder: Decoder): LinkType = decoder.decodeString().let {
                allTypes[it] ?: LinkType(it)
            }

            override fun serialize(encoder: Encoder, value: LinkType) {
                encoder.encodeString(value.value)
            }
        }
    }

    @Serializable
    data class SessionDto(val id: Long, val name: String)
}
