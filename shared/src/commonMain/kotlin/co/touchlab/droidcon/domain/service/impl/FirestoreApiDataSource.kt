package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.service.impl.dto.SponsorsDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.takeFrom
import kotlinx.serialization.json.Json

class FirestoreApiDataSource(
    private val client: HttpClient,
    private val json: Json,
) {
    // TODO: We might want to inject these constants for better reusability.
    private companion object {
        // Known variants: "sponsors", "sponsors-lisbon-2019", "sponsors-sf-2019"
        const val SPONSOR_COLLECTION_ID = "sponsors-sf-2019"
    }

    suspend fun getSponsors(): SponsorsDto.SponsorCollectionDto {
        val jsonString = client.get<String> {
            url {
                takeFrom("https://firestore.googleapis.com")
                encodedPath = "/v1/projects/droidcon-148cc/databases/(default)/documents/$SPONSOR_COLLECTION_ID?key=AIzaSyCkD5DH2rUJ8aZuJzANpIFj0AVuCNik1l0"
            }
        }
        return json.decodeFromString(SponsorsDto.SponsorCollectionDto.serializer(), jsonString)
    }
}
