package co.touchlab.droidcon.domain.service

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.service.impl.DefaultServerApi
import co.touchlab.droidcon.test.FakeUserIdProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.http.parseUrlEncodedParameters
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.readRemaining
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json

class DefaultServerApiTest {

    @Test
    fun setRsvp_attending_posts_correct_path() = runTest {
        val requests = mutableListOf<HttpRequestData>()
        val api = createApi(requests, HttpStatusCode.OK)

        val result = api.setRsvp(Session.Id("session-42"), isAttending = true)

        assertTrue(result)
        assertEquals(1, requests.size)
        val request = requests.single()
        assertEquals(HttpMethod.Post, request.method)
        assertEquals(
            "/dataTest/sessionizeRsvpEvent/session-42/test-user",
            request.url.encodedPath,
        )
    }

    @Test
    fun setRsvp_not_attending_posts_unrsvp_path() = runTest {
        val requests = mutableListOf<HttpRequestData>()
        val api = createApi(requests, HttpStatusCode.OK)

        val result = api.setRsvp(Session.Id("session-42"), isAttending = false)

        assertTrue(result)
        assertEquals(
            "/dataTest/sessionizeUnrsvpEvent/session-42/test-user",
            requests.single().url.encodedPath,
        )
    }

    @Test
    fun setFeedback_posts_form_fields() = runTest {
        val requests = mutableListOf<HttpRequestData>()
        val api = createApi(requests, HttpStatusCode.OK)

        val result = api.setFeedback(Session.Id("session-99"), rating = 3, comment = "Excellent")

        assertTrue(result)
        val request = requests.single()
        assertEquals(HttpMethod.Post, request.method)
        assertEquals(
            "/dataTest/sessionizeFeedbackEvent/session-99/test-user",
            request.url.encodedPath,
        )
        val formParameters = request.readFormParameters()
        assertEquals("3", formParameters["rating"])
        assertEquals("Excellent", formParameters["comment"])
    }

    @Test
    fun non_success_status_returns_false() = runTest {
        val api = createApi(mutableListOf(), HttpStatusCode.InternalServerError)

        val result = api.setRsvp(Session.Id("session-1"), isAttending = true)

        assertFalse(result)
    }

    @Test
    fun success_status_returns_true() = runTest {
        val api = createApi(mutableListOf(), HttpStatusCode.OK)

        val result = api.setRsvp(Session.Id("session-1"), isAttending = true)

        assertTrue(result)
    }

    private fun createApi(requests: MutableList<HttpRequestData>, status: HttpStatusCode): DefaultServerApi {
        val mockEngine = MockEngine { request ->
            requests.add(request)
            respond("OK", status)
        }
        val client = HttpClient(mockEngine)
        return DefaultServerApi(
            userIdProvider = FakeUserIdProvider("test-user"),
            client = client,
            json = Json { },
            baseUrl = "https://test.local",
        )
    }

    private suspend fun HttpRequestData.readFormParameters(): Parameters {
        val bodyText = readBodyText()
        return if (bodyText.isEmpty()) {
            Parameters.Empty
        } else {
            bodyText.parseUrlEncodedParameters()
        }
    }

    private suspend fun HttpRequestData.readBodyText(): String {
        val content = body as OutgoingContent
        return when (content) {
            is TextContent -> content.text
            is OutgoingContent.ByteArrayContent -> content.bytes().decodeToString()
            is OutgoingContent.WriteChannelContent -> {
                val channel = ByteChannel()
                content.writeTo(channel)
                channel.readRemaining().readByteArray().decodeToString()
            }
            else -> ""
        }
    }
}
