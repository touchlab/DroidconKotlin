package co.touchlab.sessionize.file

import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.ServiceRegistry.staticFileLoader
import co.touchlab.sessionize.db.SessionizeDbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object FileRepo {
    fun seedFileLoad() = CoroutineScope(ServiceRegistry.coroutinesDispatcher).launch {
        val speakerJson = staticFileLoader("speakers", "json")
        val scheduleJson = staticFileLoader("schedule", "json")
        val sponsorSessionJson = staticFileLoader("sponsor_session", "json")

        if (speakerJson != null && scheduleJson != null && sponsorSessionJson != null) {
            SessionizeDbHelper.primeAll(
                    speakerJson,
                    scheduleJson,
                   sponsorSessionJson
            )
        } else {
            //This should only ever happen in dev
            throw NullPointerException("Couldn't load static files")
        }
    }


}