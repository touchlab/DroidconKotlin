package co.touchlab.sessionize.file

import co.touchlab.sessionize.ServiceRegistry.staticFileLoader
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.jsondata.Speaker
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object FileRepo {

    private val json = Json {
        allowStructuredMapKeys = true
        ignoreUnknownKeys = true
    }

    fun seedFileLoad() {
        val speakerJson = staticFileLoader("speakers", "json")
        val scheduleJson = staticFileLoader("schedule", "json")
        val sponsorSessionJson = staticFileLoader("sponsor_session", "json")

        if (speakerJson != null && scheduleJson != null && sponsorSessionJson != null) {
            SessionizeDbHelper.primeAll(
                json.decodeFromString(speakerJson),
                json.decodeFromString(scheduleJson),
                json.decodeFromString(sponsorSessionJson)
            )
        } else {
            //This should only ever happen in dev
            throw NullPointerException("Couldn't load static files")
        }
    }


}