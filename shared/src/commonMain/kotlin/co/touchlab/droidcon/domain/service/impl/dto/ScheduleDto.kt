package co.touchlab.droidcon.domain.service.impl.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

object ScheduleDto {

    @Serializable
    data class DayDto(
        val date: String,
        val rooms: List<RoomDto>,
    )

    @Serializable
    data class RoomDto(
        val id: Long,
        val name: String,
        val sessions: List<SessionDto>,
    )

    @Serializable
    data class SessionDto(
        val id: String,
        val title: String,
        val description: String? = null,
        // @Serializable(with = InstantIso8601Serializer::class)
        val startsAt: String,
        // @Serializable(with = InstantIso8601Serializer::class)
        val endsAt: String,
        val isServiceSession: Boolean,
        val isPlenumSession: Boolean,
        val speakers: List<SpeakerDto>,
        val categories: JsonArray,

        @SerialName("roomId")
        val roomID: Long,
        val room: String,
    )

    @Serializable
    data class SpeakerDto(
        val id: String,
        val name: String,
    )

    fun List<DayDto>.sessions(): List<SessionDto> = flatMap { day ->
        day.rooms.flatMap { room ->
            room.sessions
        }
    }
}
