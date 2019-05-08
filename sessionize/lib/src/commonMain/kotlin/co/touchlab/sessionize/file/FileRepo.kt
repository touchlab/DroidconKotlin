package co.touchlab.sessionize.file

import co.touchlab.sessionize.ServiceRegistry.staticFileLoader
import co.touchlab.sessionize.db.SessionizeDbHelper
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object FileRepo {
    fun seedFileLoad() {
        val sponsorJson = staticFileLoader("sponsors", "json")
        val speakerJson = staticFileLoader("speakers", "json")
        val scheduleJson = staticFileLoader("schedule", "json")

        if (sponsorJson != null && speakerJson != null && scheduleJson != null) {
            SessionizeDbHelper.primeAll(speakerJson, scheduleJson, sponsorJson)
        } else {
            //This should only ever happen in dev
            throw NullPointerException("Couldn't load static files")
        }
    }
}