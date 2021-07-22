package co.touchlab.sessionize.file

import co.touchlab.sessionize.BaseModel
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.staticFileLoader

class FileRepo(private val dbHelper: SessionizeDbHelper): BaseModel() {
    fun seedFileLoad() {
        val speakerJson = staticFileLoader("speakers", "json")
        val scheduleJson = staticFileLoader("schedule", "json")
        val sponsorSessionJson = staticFileLoader("sponsor_session", "json")

        if (speakerJson != null && scheduleJson != null && sponsorSessionJson != null) {
            dbHelper.primeAll(
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