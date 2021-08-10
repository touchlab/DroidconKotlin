package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.Constants
import co.touchlab.droidcon.domain.service.impl.dto.SponsorsDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.takeFrom
import kotlinx.serialization.json.Json

class FirestoreApiDataSource(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun getSponsors(): SponsorsDto.SponsorCollectionDto {
        val jsonString = client.get<String> {
            url {
                takeFrom("https://firestore.googleapis.com")
                with(Constants.Firestore) {
                    encodedPath = "/v1/projects/$projectId/databases/$databaseName/documents/$collectionName?key=$apiKey"
                }
            }
        }
        return json.decodeFromString(SponsorsDto.SponsorCollectionDto.serializer(), jsonString)
    }
}
