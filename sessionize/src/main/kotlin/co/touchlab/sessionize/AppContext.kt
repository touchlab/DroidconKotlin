package co.touchlab.sessionize

import co.touchlab.sessionize.db.NoteDbHelper
import co.touchlab.sessionize.platform.*

object AppContext {

    val dbHelper = NoteDbHelper()

    val appSettings = settingsFactory().create("DROIDCON_SETTINGS")
    val KEY_FIRST_RUN = "FIRST_RUN1"
    val USER_UUID = "USER_UUID"
    private val SPONSOR_JSON = "SPONSOR_JSON"

    lateinit var staticFileLoader: (filePrefix: String, fileType: String) -> String?
    lateinit var analyticsCallback: (name: String, params: Map<String, Any>) -> Unit

    fun initPlatformClient(staticFileLoader: (filePrefix: String, fileType: String) -> String?,
                           analyticsCallback: (name: String, params: Map<String, Any>) -> Unit) {
        this.staticFileLoader = staticFileLoader
        this.analyticsCallback = analyticsCallback

        dataLoad()
    }

    fun logEvent(name: String, params: Map<String, Any>) {
        analyticsCallback(name, params)
    }

    private fun firstRun(): Boolean = appSettings.getBoolean(KEY_FIRST_RUN, true)

    private fun updateFirstRun() {
        appSettings.putBoolean(KEY_FIRST_RUN, false)
    }

    public fun userUuid(): String {
        if (appSettings.getString(USER_UUID).isBlank()) {
            appSettings.putString(USER_UUID, createUuid())
        }
        return appSettings.getString(USER_UUID)
    }

    val sponsorJson:String
        get() = appSettings.getString(SPONSOR_JSON)

    //Split these up so they can individually succeed/fail
    private fun dataLoad(){
        networkBackgroundTask {
            try {
                if (firstRun()) {
                    val sponsorJson = staticFileLoader("sponsors", "json")
                    val speakerJson = staticFileLoader("speakers", "json")
                    val scheduleJson = staticFileLoader("schedule", "json")

                    if (sponsorJson != null && speakerJson != null && scheduleJson != null) {
                        storeAll(sponsorJson, speakerJson, scheduleJson)
                        updateFirstRun()
                    } else {
                        //This should only ever happen in dev
                        throw NullPointerException("Couldn't load static files")
                    }
                }
            } catch (e: Exception) {
                logException(e)
            }

            try {
                val networkSpeakerJson = simpleGet(
                        "https://sessionize.com/api/v2/$SESSIONIZE_INSTANCE_ID/view/speakers"
                )

                val networkSessionJson = simpleGet(
                        "https://sessionize.com/api/v2/$SESSIONIZE_INSTANCE_ID/view/gridtable"
                )

                val networkSponsorJson = simpleGet(
                        "https://s3.amazonaws.com/droidconsponsers/sponsors.json"
                )

                storeAll(networkSponsorJson, networkSpeakerJson, networkSessionJson)

            } catch (e: Exception) {
                logException(e)
            }
        }
    }

    fun storeAll(networkSponsorJson: String, networkSpeakerJson: String, networkSessionJson: String) {
        appSettings.putString(SPONSOR_JSON, networkSponsorJson)
        dbHelper.primeAll(networkSpeakerJson, networkSessionJson)
    }
}

