package co.touchlab.notepad

import co.touchlab.notepad.db.NoteDbHelper
import co.touchlab.notepad.utils.backgroundTask
import co.touchlab.notepad.utils.logException
import co.touchlab.notepad.utils.simpleGet

object AppContext{
    val dbHelper = NoteDbHelper()

    fun primeData(speakerJson:String, scheduleJson:String){
        backgroundTask {
            dbHelper.primeAll(speakerJson, scheduleJson)

            try {
                val networkSpeakerJson = simpleGet(
                        "https://sessionize.com/api/v2/tovwb4kd/view/speakers"
                )

                val networkSessionJson = simpleGet(
                        "https://sessionize.com/api/v2/tovwb4kd/view/gridtable"
                )

                dbHelper.primeAll(networkSpeakerJson, networkSessionJson)
            } catch (e: Exception) {
                logException(e)
            }
        }
    }
}
