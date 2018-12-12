package co.touchlab.sessionize

import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.platform.*
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.freeze
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import timber.log.info

object AppContext {

    val dbHelper = SessionizeDbHelper()

    val appSettings = settingsFactory().create("DROIDCON_SETTINGS")
    val KEY_FIRST_RUN = "FIRST_RUN1"
    val KEY_LAST_LOAD = "LAST_LOAD"
    val USER_UUID = "USER_UUID"
    val TWO_HOURS_MILLIS = 2 * 60 * 60 * 1000

    private val SPONSOR_JSON = "SPONSOR_JSON"

    val lambdas = AtomicReference<PlatformLambdas?>(null)
    val dispatcherLocal = ThreadLocalRef<CoroutineDispatcher>()

    fun initPlatformClient(
            staticFileLoader: (filePrefix: String, fileType: String) -> String?,
            analyticsCallback: (name: String, params: Map<String, Any>) -> Unit,
            clLogCallback: (s: String) -> Unit,
            dispatcher: CoroutineDispatcher) {

        lambdas.value = PlatformLambdas(
                staticFileLoader,
                analyticsCallback,
                clLogCallback).freeze()

        dispatcherLocal.value = dispatcher

        dataLoad()

        Timber.info { "Init complete" }
    }

    data class PlatformLambdas(val staticFileLoader: (filePrefix: String, fileType: String) -> String?,
                               val analyticsCallback: (name: String, params: Map<String, Any>) -> Unit,
                               val clLogCallback: (s: String) -> Unit)

    val staticFileLoader: (filePrefix: String, fileType: String) -> String?
        get() = lambdas.value!!.staticFileLoader

    val clLogCallback: (s: String) -> Unit
        get() = lambdas.value!!.clLogCallback

    fun logEvent(name: String, params: Map<String, Any>) {
        lambdas.value!!.analyticsCallback(name, params)
    }

    private fun firstRun(): Boolean = appSettings.getBoolean(KEY_FIRST_RUN, true)

    private fun updateFirstRun() {
        appSettings.putBoolean(KEY_FIRST_RUN, false)
    }

    fun userUuid(): String {
        if (appSettings.getString(USER_UUID).isBlank()) {
            appSettings.putString(USER_UUID, createUuid())
        }
        return appSettings.getString(USER_UUID)
    }

    val sponsorJson: String
        get() = appSettings.getString(SPONSOR_JSON)

    //Split these up so they can individually succeed/fail
    private fun dataLoad() {
        networkBackgroundTask {
            try {
                if (firstRun()) {
                    val staticFileLoader = lambdas.value!!.staticFileLoader
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
        }
    }

    private fun dataCalls() {
        try {
            val networkSpeakerJson = simpleGet(
                    "https://sessionize.com/api/v2/$SESSIONIZE_INSTANCE_ID/view/speakers"
            )

            val networkSessionJson = simpleGet(
                    "https://sessionize.com/api/v2/$SESSIONIZE_INSTANCE_ID/view/gridtable"
            )

            val networkSponsorJson = simpleGet(
                    "https://s3.amazonaws.com/droidconsponsers/sponsors-$SESSIONIZE_INSTANCE_ID.json"
            )

            storeAll(networkSponsorJson, networkSpeakerJson, networkSessionJson)
            appSettings.putLong(KEY_LAST_LOAD, currentTimeMillis())
        } catch (e: Exception) {
            logException(e)
        }
    }

    fun refreshData() {
        val lastLoad = appSettings.getLong(KEY_LAST_LOAD)
        if (lastLoad < (currentTimeMillis() - (TWO_HOURS_MILLIS.toLong()))) {
            networkBackgroundTask {
                dataCalls()
            }
        }
    }

    fun storeAll(networkSponsorJson: String, networkSpeakerJson: String, networkSessionJson: String) {
        appSettings.putString(SPONSOR_JSON, networkSponsorJson)
        dbHelper.primeAll(networkSpeakerJson, networkSessionJson)
    }
}

/**
 * Log statement to Crashlytics
 */
fun clLog(s: String) {
    AppContext.clLogCallback(s)
}