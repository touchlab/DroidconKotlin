package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.Constants
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.service.impl.dto.RSVPSDto
import co.touchlab.droidcon.domain.service.impl.dto.ScheduleDto
import co.touchlab.droidcon.domain.service.impl.dto.SpeakersDto
import co.touchlab.droidcon.domain.service.impl.dto.SponsorSessionsDto
import co.touchlab.droidcon.domain.service.impl.dto.SponsorsDto
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.math.log

class DefaultApiDataSource(
    private val client: HttpClient,
    private val json: Json,
) : DefaultSyncService.DataSource {
    override suspend fun getSpeakers(): List<SpeakersDto.SpeakerDto> {
        val jsonString = client.get {
            // We want to use the `sponsorsId` to get "speakers" for the sponsors as well as speakers for real sessions.
            sessionize("/api/v2/${Constants.Sessionize.sponsorsId}/view/speakers")
        }.bodyAsText()
        return json.decodeFromString(
            ListSerializer(SpeakersDto.SpeakerDto.serializer()),
            jsonString
        )
    }

    override suspend fun getSchedule(): List<ScheduleDto.DayDto> {
        val jsonString = client.get {
            sessionize("/api/v2/${Constants.Sessionize.scheduleId}/view/gridtable")
        }.bodyAsText()
        return json.decodeFromString(ListSerializer(ScheduleDto.DayDto.serializer()), jsonString)
    }

    override suspend fun getSponsorSessions(): List<SponsorSessionsDto.SessionGroupDto> {
        val jsonString = client.get {
            sessionize("/api/v2/${Constants.Sessionize.sponsorsId}/view/sessions")
        }.bodyAsText()
        return json.decodeFromString(
            ListSerializer(SponsorSessionsDto.SessionGroupDto.serializer()),
            jsonString
        )
    }

    override suspend fun getSponsors(): SponsorsDto.SponsorCollectionDto {
        val jsonString = client.get {
            with(Constants.Firestore) {
                firestore("/v1/projects/$projectId/databases/$databaseName/documents/$collectionName?key=$apiKey")
            }
        }.bodyAsText()
        return json.decodeFromString(SponsorsDto.SponsorCollectionDto.serializer(), jsonString)
    }

    override suspend fun getRSVPs(userId: String): RSVPSDto.RSVPsCollectionDto {
        val jsonString = client.get {
            with(Constants.Firestore) {
                firestore("/v1/projects/$projectId/databases/$databaseName/documents/$rsvpName/$userId?key=$apiKey")
            }
        }.bodyAsText()
        return json.decodeFromString(RSVPSDto.RSVPsCollectionDto.serializer(), jsonString)
    }


    override suspend fun setRSVPs(userId: String, sessionId: Session.Id, rsvp: Boolean) {

        val rsvps = getRSVPs(userId).copyWithSession(sessionId.value, rsvp)
        client.post {
            with(Constants.Firestore) {
                firestore("/v1/projects/$projectId/databases/$databaseName/documents/$rsvpName/$userId?key=$apiKey")
            }
            setBody(
                json.encodeToString(RSVPSDto.RSVPsCollectionDto.serializer(), rsvps)
            )
        }
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
