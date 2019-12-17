package co.touchlab.sessionize.api

import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys
import co.touchlab.sessionize.jsondata.Days
import co.touchlab.sessionize.jsondata.Session
import co.touchlab.sessionize.platform.createUuid
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.response.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import kotlinx.io.core.use
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object SessionizeApiImpl : SessionizeApi {
    private val INSTANCE_ID = "jmuc9diq"
    private val SPONSOR_INSTANCE_ID = "lhiyghwr"
    private val client = HttpClient()

    override suspend fun getSpeakersJson(): String = client.get<String> {
        sessionize("/api/v2/$SPONSOR_INSTANCE_ID/view/speakers")
    }

    override suspend fun getSessionsJson(): String = client.get<String> {
        sessionize("/api/v2/$INSTANCE_ID/view/gridtable")
    }

    override suspend fun getSponsorSessionJson(): String = client.get<String> {
        sessionize("/api/v2/$SPONSOR_INSTANCE_ID/view/sessions")
    }

    override suspend fun recordRsvp(methodName: String, sessionId: String): Boolean = client.request<HttpResponse> {
        droidcon("/dataTest/$methodName/$sessionId/${userUuid()}")
        method = HttpMethod.Post
        body = ""
    }.use {
        it.status.isSuccess()
    }

    override suspend fun sendFeedback(sessionId: String, rating: Int, comment: String?): Boolean = client.submitForm<HttpResponse>(formParameters = Parameters.build {
        append("rating", rating.toString())
        append("comment", comment.orEmpty())
    }) {
        droidcon("/dataTest/sessionizeFeedbackEvent/$sessionId/${userUuid()}")
    }.use {
        it.status.isSuccess()
    }

    private fun HttpRequestBuilder.sessionize(path: String) {
        url {
            takeFrom("https://sessionize.com")
            encodedPath = path
        }
    }

    private fun HttpRequestBuilder.amazon(path: String) {
        url {
            takeFrom("https://s3.amazonaws.com")
            encodedPath = path
        }
    }

    private fun HttpRequestBuilder.droidcon(path: String) {
        url {
            takeFrom("https://droidcon-server.herokuapp.com")
            encodedPath = path
        }
    }

    private fun HttpRequestBuilder.github(path: String) {
        url {
            takeFrom("https://raw.githubusercontent.com/touchlab/DroidconKotlin")
            encodedPath = path
        }
    }
}

internal fun userUuid(): String {
    if (ServiceRegistry.appSettings.getString(SettingsKeys.USER_UUID).isBlank()) {
        ServiceRegistry.appSettings.putString(SettingsKeys.USER_UUID, createUuid())
    }
    return ServiceRegistry.appSettings.getString(SettingsKeys.USER_UUID)
}

internal fun parseSessionsFromDays(scheduleJson: String): List<Session> {
    val days = Json.nonstrict.parse(Days.serializer().list, scheduleJson)
    val sessions = mutableListOf<Session>()

    days.forEach { day ->
        day.rooms.forEach { room ->
            sessions.addAll(room.sessions)
        }
    }

    return sessions
}
