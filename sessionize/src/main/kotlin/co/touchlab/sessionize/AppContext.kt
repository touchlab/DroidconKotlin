package co.touchlab.sessionize

import co.touchlab.sessionize.db.NoteDbHelper
import co.touchlab.sessionize.utils.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTreeParser

object AppContext{

    val dbHelper = NoteDbHelper()

    val appSettings = settingsFactory().create("DROIDCON_SETTINGS")
    val KEY_FIRST_RUN = "FIRST_RUN1"
    lateinit var staticFileLoader:(filePrefix:String, fileType:String)->String?

    fun initPlatformClient(staticFileLoader:(filePrefix:String, fileType:String)->String?){
        this.staticFileLoader = staticFileLoader
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

    /**
     * Should be called in background. Kind of hacky, but works.
     */
    fun parseAbout():List<AboutInfo>{
        val aboutJson = staticFileLoader("about", "json")!!
        val aboutList = ArrayList<AboutInfo>()

        val json = JsonTreeParser(aboutJson).readFully()

        (json as JsonArray).forEach {
            val aboutJson = it as JsonObject
            aboutList.add(
                    AboutInfo(
                            aboutJson.getAsValue("icon").content,
                            aboutJson.getAsValue("title").content,
                            aboutJson.getAsValue("detail").content
                    )
            )
        }

        return aboutList
    }
}

data class AboutInfo(val icon:String, val title:String, val detail:String)