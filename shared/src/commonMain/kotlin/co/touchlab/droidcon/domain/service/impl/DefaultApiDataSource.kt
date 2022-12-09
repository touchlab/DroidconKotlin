package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.Constants
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
    private val json: Json
) : DefaultSyncService.DataSource {
    override suspend fun getSpeakers(): List<SpeakersDto.SpeakerDto> {
        val jsonString = client.get {
            // We want to use the `sponsorsId` to get "speakers" for the sponsors as well as speakers for real sessions.
            sessionize("/api/v2/${Constants.sessionizeSponsorsId}/view/speakers")
        }.bodyAsText()
        return json.decodeFromString(ListSerializer(SpeakersDto.SpeakerDto.serializer()), jsonString)
    }

    override suspend fun getSchedule(): List<ScheduleDto.DayDto> {
        val jsonString = client.get {
            sessionize("/api/v2/${Constants.sessionizeScheduleId}/view/gridtable")
        }.bodyAsText()
        return json.decodeFromString(ListSerializer(ScheduleDto.DayDto.serializer()), jsonString)
    }

    override suspend fun getSponsorSessions(): List<SponsorSessionsDto.SessionGroupDto> {
        val jsonString = client.get {
            sessionize("/api/v2/${Constants.sessionizeSponsorsId}/view/sessions")
        }.bodyAsText()
        return json.decodeFromString(ListSerializer(SponsorSessionsDto.SessionGroupDto.serializer()), jsonString)
    }

    override suspend fun getSponsors(): SponsorsDto.SponsorCollectionDto {
        val jsonString = client.get {
            firestore("/v1/projects/${Constants.firestoreProjectId}/databases/${Constants.firestoreDatabaseName}/documents/${Constants.firestoreCollectionName}?key=${Constants.firestoreApiKey}")
        }.bodyAsText()
        return json.decodeFromString(SponsorsDto.SponsorCollectionDto.serializer(), jsonString)
    }

    private fun HttpRequestBuilder.sessionize(path: String) {
        url {
            takeFrom(Constants.sessionizeUrl)
            encodedPath = path
        }
    }

    private fun HttpRequestBuilder.firestore(path: String) {
        url {
            takeFrom(Constants.firestoreUrl)
            encodedPath = path
        }
    }
}
