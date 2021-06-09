package co.touchlab.sessionize.jsondata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Days(val date: String, val rooms: List<Room>)

@Serializable
data class Room(val id: Int, val name: String, val sessions: List<Session>)

@Serializable
data class Session(
        val id: String,
        val title: String,
        @SerialName("description")
        val descriptionText: String?,
        val startsAt: String?,
        val endsAt: String?,
        val isServiceSession: Boolean,
        val speakers: List<SessionSpeaker>,
        val roomId: Int?,
        val room: String
)

@Serializable
data class SessionSpeaker(
        val id: String,
        val name: String
)

@Serializable
data class Speaker(
        val id: String,
        val firstName: String,
        val lastName: String,
        val fullName: String,
        val bio: String,
        val tagLine: String,
        val profilePicture: String?,
        val links: List<SpeakerLink>
)

@Serializable
data class SpeakerLink(
        val title: String,
        val url: String,
        val linkType: String
)

@Serializable
data class SponsorSessionGroup(
        val sessions: List<SponsorSession>
)

@Serializable
data class SponsorSession(
        val id: String,
        val title: String,
        @SerialName("description")
        val descriptionText: String?,
        val speakers: List<SessionSpeaker>
)
