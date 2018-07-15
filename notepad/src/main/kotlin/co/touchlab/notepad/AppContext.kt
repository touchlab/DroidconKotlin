package co.touchlab.notepad

import co.touchlab.notepad.db.NoteDbHelper
import co.touchlab.notepad.utils.backgroundTask

object AppContext{
    val dbHelper = NoteDbHelper()

    fun primeData(speakerJson:String, scheduleJson:String){
        backgroundTask {
            dbHelper.primeAll(speakerJson, scheduleJson)
        }
    }
}