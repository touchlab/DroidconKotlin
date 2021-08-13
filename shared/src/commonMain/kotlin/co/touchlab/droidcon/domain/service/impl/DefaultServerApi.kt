package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.service.ServerApi
import co.touchlab.droidcon.domain.service.UserIdProvider
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import kotlinx.serialization.json.Json

class DefaultServerApi(
    private val userIdProvider: UserIdProvider,
    private val client: HttpClient,
    private val json: Json,
): ServerApi {
    override suspend fun setRsvp(sessionId: Session.Id, isAttending: Boolean): Boolean {
        val methodName = if (isAttending) {
            "sessionizeRsvpEvent"
        } else {
            "sessionizeUnrsvpEvent"
        }

        return client.submitForm<HttpResponse> {
            droidcon("/dataTest/$methodName/${sessionId.value}/${userIdProvider.getId()}")
            method = HttpMethod.Post
            body = ""
        }.status.isSuccess()
    }

    override suspend fun setFeedback(sessionId: Session.Id, rating: Int, comment: String): Boolean {
        return client.submitForm<HttpResponse>(formParameters = Parameters.build {
            append("rating", rating.toString())
            append("comment", comment)
        }) {
            droidcon("/dataTest/sessionizeFeedbackEvent/${sessionId.value}/${userIdProvider.getId()}")
        }.status.isSuccess()
    }

    private fun HttpRequestBuilder.droidcon(path: String) {
        url {
            takeFrom("https://droidcon-server.herokuapp.com")
            encodedPath = path
        }
    }
}