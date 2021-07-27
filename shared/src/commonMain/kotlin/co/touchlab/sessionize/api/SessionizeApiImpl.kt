package co.touchlab.sessionize.api

import co.touchlab.sessionize.SettingsKeys
import co.touchlab.sessionize.jsondata.Days
import co.touchlab.sessionize.jsondata.Session
import co.touchlab.sessionize.platform.createUuid
import co.touchlab.sessionize.platform.simpleGet
import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.native.concurrent.ThreadLocal

class SessionizeApiImpl(private val appSettings:Settings) : SessionizeApi {
    private val INSTANCE_ID = "jmuc9diq"
    private val SPONSOR_INSTANCE_ID = "lhiyghwr"
    private val client = HttpClient {
//        install(ExpectSuccess)
    }

    override suspend fun getSpeakersJson(): String = client.get<String> {
        sessionize("/api/v2/$SPONSOR_INSTANCE_ID/view/speakers")
    }

    override suspend fun getSessionsJson(): String = client.get<String> {
        sessionize("/api/v2/$INSTANCE_ID/view/gridtable")
    }

    override suspend fun getSponsorSessionJson(): String = client.get<String> {
        sessionize("/api/v2/$SPONSOR_INSTANCE_ID/view/sessions")
    }

    override suspend fun recordRsvp(methodName: String, sessionId: String): Boolean = true/* = client.request<HttpResponse> {
        droidcon("/dataTest/$methodName/$sessionId/${userUuid()}")
        method = HttpMethod.Post
        body = ""
    }.use {
        it.status.isSuccess()
    }*/

    override suspend fun sendFeedback(sessionId: String, rating: Int, comment: String?): Boolean = true /*client.submitForm<HttpResponse>(formParameters = Parameters.build {
        append("rating", rating.toString())
        append("comment", comment.orEmpty())
    }) {
        droidcon("/dataTest/sessionizeFeedbackEvent/$sessionId/${userUuid()}")
    }.use {
        it.status.isSuccess()
    }*/

    private fun HttpRequestBuilder.sessionize(path: String) {
        url {
            takeFrom("https://sessionize.com")
            encodedPath = path
        }
    }

    internal fun userUuid(): String {
        if (appSettings.getString(SettingsKeys.USER_UUID).isBlank()) {
            appSettings.putString(SettingsKeys.USER_UUID, createUuid())
        }
        return appSettings.getString(SettingsKeys.USER_UUID)
    }
}

@ThreadLocal
private val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
}

internal fun parseSessionsFromDays(scheduleJson: String): List<Session> {
    val days = json.decodeFromString(ListSerializer(Days.serializer()), scheduleJson)
    val sessions = mutableListOf<Session>()

    days.forEach { day ->
        day.rooms.forEach { room ->
            sessions.addAll(room.sessions)
        }
    }

    return sessions
}
