package co.touchlab.sessionize

import co.touchlab.sessionize.db.NoteDbHelper
import co.touchlab.sessionize.utils.*

object AppContext{

    val dbHelper = NoteDbHelper()

    val appSettings = settingsFactory().create("DROIDCON_SETTINGS")
    val KEY_FIRST_RUN = "FIRST_RUN1"

    fun initPlatformClient(staticFileLoader:(filePrefix:String, fileType:String)->String?){
        if(firstRun()){
            val speakerJson = staticFileLoader("speakers", "json")
            val scheduleJson = staticFileLoader("schedule", "json")
            if(speakerJson != null && scheduleJson != null) {
                primeData(speakerJson = speakerJson,
                        scheduleJson = scheduleJson)
            }else{
                throw NullPointerException("Couldn't load static files")
            }
        }else{
            networkDataUpdate()
        }
    }

    private fun firstRun():Boolean = appSettings.getBoolean(KEY_FIRST_RUN, true)

    private fun updateFirstRun(){
        appSettings.putBoolean(KEY_FIRST_RUN, false)
    }

    private fun primeData(speakerJson:String, scheduleJson:String){
        backgroundTask({
            dbHelper.primeAll(speakerJson, scheduleJson)
        }){
            updateFirstRun()
        }
    }

    private fun networkDataUpdate(){
        networkBackgroundTask {
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
