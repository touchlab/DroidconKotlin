package co.touchlab.sessionize

import co.touchlab.sessionize.db.NoteDbHelper
import co.touchlab.sessionize.platform.*

object AppContext{

    val dbHelper = NoteDbHelper()

    val appSettings = settingsFactory().create("DROIDCON_SETTINGS")
    val KEY_FIRST_RUN = "FIRST_RUN1"
    lateinit var staticFileLoader:(filePrefix:String, fileType:String)->String?
    lateinit var analyticsCallback:(name: String, params: Map<String, Any>)->Unit

    fun initPlatformClient(staticFileLoader:(filePrefix:String, fileType:String)->String?,
                           analyticsCallback:(name: String, params: Map<String, Any>)->Unit){
        this.staticFileLoader = staticFileLoader
        this.analyticsCallback = analyticsCallback
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

    fun logEvent(name: String, params: Map<String, Any>){
        analyticsCallback(name, params)
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
                        "https://sessionize.com/api/v2/$SESSIONIZE_INSTANCE_ID/view/speakers"
                )

                val networkSessionJson = simpleGet(
                        "https://sessionize.com/api/v2/$SESSIONIZE_INSTANCE_ID/view/gridtable"
                )

                dbHelper.primeAll(networkSpeakerJson, networkSessionJson)
            } catch (e: Exception) {
                logException(e)
            }
        }
    }
}

