package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.impl.dto.ScheduleDto
import co.touchlab.droidcon.domain.service.impl.dto.SpeakersDto
import co.touchlab.droidcon.domain.service.impl.dto.SponsorSessionsDto
import co.touchlab.droidcon.domain.service.impl.dto.SponsorsDto
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class DefaultApiDataSource(
    private val client: HttpClient,
    private val json: Json,
    private val conferenceConfigProvider: ConferenceConfigProvider,
) : DefaultSyncService.DataSource {
    override suspend fun getSpeakers(): List<SpeakersDto.SpeakerDto> {
        val jsonString = client.get {
            // We want to use the same scheduleId for speakers and schedule
            sessionize("/api/v2/${conferenceConfigProvider.getScheduleId()}/view/speakers")
        }.bodyAsText()
        return json.decodeFromString(ListSerializer(SpeakersDto.SpeakerDto.serializer()), jsonString)
    }

    override suspend fun getSchedule(): List<ScheduleDto.DayDto> {
        val jsonString = client.get {
            sessionize("/api/v2/${conferenceConfigProvider.getScheduleId()}/view/gridtable")
        }.bodyAsText()
        return json.decodeFromString(ListSerializer(ScheduleDto.DayDto.serializer()), jsonString)
    }

    override suspend fun getSponsorSessions(): List<SponsorSessionsDto.SessionGroupDto> {
        val jsonString = client.get {
            sessionize("/api/v2/${conferenceConfigProvider.getScheduleId()}/view/sessions")
        }.bodyAsText()
        return json.decodeFromString(ListSerializer(SponsorSessionsDto.SessionGroupDto.serializer()), jsonString)
    }

    override suspend fun getSponsors(): SponsorsDto.SponsorCollectionDto {
        val projectId = conferenceConfigProvider.getProjectId()
        val collectionName = conferenceConfigProvider.getCollectionName()
        val apiKey = conferenceConfigProvider.getApiKey()
        val databaseName = "(default)" // This could be moved to ConferenceConfigProvider if needed

        val jsonString = client.get {
            firestore("/v1/projects/$projectId/databases/$databaseName/documents/$collectionName?key=$apiKey")
        }.bodyAsText()
        return json.decodeFromString(SponsorsDto.SponsorCollectionDto.serializer(), jsonString)
    }

    private fun HttpRequestBuilder.sessionize(path: String) {
        url {
            takeFrom("https://sessionize.com")
            encodedPath = path
        }
    }

    private fun HttpRequestBuilder.firestore(path: String) {
        url {
            takeFrom("https://firestore.googleapis.com")
            encodedPath = path
        }
    }
}
