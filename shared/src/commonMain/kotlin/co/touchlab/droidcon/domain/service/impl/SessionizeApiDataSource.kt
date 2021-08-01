package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.service.impl.dto.ScheduleDto
import co.touchlab.droidcon.domain.service.impl.dto.SpeakersDto
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.takeFrom
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class SessionizeApiDataSource(
    private val client: HttpClient,
    private val json: Json,
): SessionizeSyncService.DataSource {
    // TODO: We might want to inject these constants for better reusability.
    private companion object {
        const val INSTANCE_ID = "jmuc9diq"
    }

    override suspend fun getSpeakers(): List<SpeakersDto.SpeakerDto> {
        val jsonString = client.get<String> {
            sessionize("/api/v2/$INSTANCE_ID/view/speakers")
        }
        return json.decodeFromString(ListSerializer(SpeakersDto.SpeakerDto.serializer()), jsonString)
    }

    override suspend fun getSchedule(): List<ScheduleDto.DayDto> {
        val jsonString = client.get<String> {
            sessionize("/api/v2/$INSTANCE_ID/view/gridtable")
        }
        return json.decodeFromString(ListSerializer(ScheduleDto.DayDto.serializer()), jsonString)
    }

    private fun HttpRequestBuilder.sessionize(path: String) {
        url {
            takeFrom("https://sessionize.com")
            encodedPath = path
        }
    }
}