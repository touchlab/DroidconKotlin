package co.touchlab.sessionize.api

import co.touchlab.sessionize.AppContext
import co.touchlab.sessionize.jsondata.Days
import co.touchlab.sessionize.jsondata.Session
import co.touchlab.stately.freeze
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.response.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import kotlinx.io.core.use
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object SessionizeApiImpl : SessionizeApi {
    private val INSTANCE_ID = "4q6ac2c0"
    private val client = HttpClient {
        install(ExpectSuccess)
    }.freeze()

    override suspend fun getSpeakersJson(): String = client.get<String> {
        sessionize("/api/v2/$INSTANCE_ID/view/speakers")
    }

    override suspend fun getSessionsJson(): String = client.get<String> {
        sessionize("/api/v2/$INSTANCE_ID/view/gridtable")
    }

    override suspend fun getSponsorJson(): String = client.get<String> {
        amazon("/droidconsponsers/sponsors-$INSTANCE_ID.json")
    }

    override suspend fun recordRsvp(methodName: String, sessionId: String): Boolean = client.request<HttpResponse> {
        droidcon("/dataTest/$methodName/$sessionId/${AppContext.userUuid()}")
        method = HttpMethod.Post
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

}

internal fun parseSessionsFromDays(scheduleJson: String): List<Session> {
    val days = JSON.nonstrict.parse(Days.serializer().list, scheduleJson)
    val sessions = mutableListOf<Session>()

    days.forEach { day ->
        day.rooms.forEach { room ->
            sessions.addAll(room.sessions)
        }
    }

    return sessions
}
