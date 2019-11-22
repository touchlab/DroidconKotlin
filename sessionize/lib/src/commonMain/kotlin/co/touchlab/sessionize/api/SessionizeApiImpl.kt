package co.touchlab.sessionize.api

import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys
import co.touchlab.sessionize.jsondata.Days
import co.touchlab.sessionize.jsondata.Session
import co.touchlab.sessionize.platform.createUuid
import co.touchlab.sessionize.platform.networkDispatcher
import co.touchlab.sessionize.platform.simpleGet
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object SessionizeApiImpl : SessionizeApi {
    private val INSTANCE_ID = "jmuc9diq"
    private val SPONSOR_INSTANCE_ID = "lhiyghwr"
//    private val client = HttpClient {
//        install(ExpectSuccess)
//    }

    override suspend fun getSpeakersJson(): String = withContext(networkDispatcher()) {
        simpleGet("https://sessionize.com/api/v2/$SPONSOR_INSTANCE_ID/view/speakers")
    }

    override suspend fun getSessionsJson(): String = withContext(networkDispatcher()) {
        simpleGet("https://sessionize.com/api/v2/$INSTANCE_ID/view/gridtable")
    }

    override suspend fun getSponsorSessionJson(): String = withContext(networkDispatcher()) {
        simpleGet("https://sessionize.com/api/v2/$SPONSOR_INSTANCE_ID/view/sessions")
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
