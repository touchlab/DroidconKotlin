package co.touchlab.sessionize

import co.touchlab.droidcon.db.RoomQueries
import co.touchlab.droidcon.db.SessionQueries
import co.touchlab.droidcon.db.UserAccountQueries
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.platform.*
import co.touchlab.stately.annotation.ThreadLocal
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.freeze
import co.touchlab.stately.isNativeFrozen
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

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
    val coroutineScope = ThreadLocalRef<CoroutineScope>()
    val sessionizeApi = ThreadLocalRef<SessionizeApi>()

    fun initPlatformClient(
            staticFileLoader: (filePrefix: String, fileType: String) -> String?,
            analyticsCallback: (name: String, params: Map<String, Any>) -> Unit,
            clLogCallback: (s: String) -> Unit,
            dispatcher: CoroutineDispatcher,
            sqlDriver: SqlDriver) {

        dbHelper.initDatabase(sqlDriver)

        lambdas.value = PlatformLambdas(
                staticFileLoader,
                analyticsCallback,
                clLogCallback).freeze()

        dispatcherLocal.value = dispatcher
        coroutineScope.value = AppContextCoroutineScope(dispatcher)
        sessionizeApi.value = SessionizeApi

        dataLoad()
    }

    internal val sessionQueries: SessionQueries
        get() = AppContext.dbHelper.instance.sessionQueries

    internal val userAccountQueries: UserAccountQueries
        get() = AppContext.dbHelper.instance.userAccountQueries

    internal val roomQueries: RoomQueries
        get() = AppContext.dbHelper.instance.roomQueries

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
        if(firstRun()){
            backgroundTask({
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
            }){
                refreshData()
            }
        }
    }

    private fun dataCalls() = coroutineScope.lateValue.launch {
        try {

            val networkSpeakerJson = sessionizeApi.lateValue.getSpeakersJson()
            val networkSessionJson = sessionizeApi.lateValue.getSessionsJson()
            val networkSponsorJson = sessionizeApi.lateValue.getSponsorJson()

            backgroundSuspend {
                storeAll(networkSponsorJson, networkSpeakerJson, networkSessionJson)
                appSettings.putLong(KEY_LAST_LOAD, currentTimeMillis())
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    fun refreshData() {
        if(!firstRun()) {
            val lastLoad = appSettings.getLong(KEY_LAST_LOAD)
            if (lastLoad < (currentTimeMillis() - (TWO_HOURS_MILLIS.toLong()))) {
                dataCalls()
            }
        }
    }

    private fun storeAll(networkSponsorJson: String, networkSpeakerJson: String, networkSessionJson: String) {
        appSettings.putString(SPONSOR_JSON, networkSponsorJson)
        dbHelper.primeAll(networkSpeakerJson, networkSessionJson)
    }
}

val <T> ThreadLocalRef<T>.lateValue:T
    get() = this.value!!

internal class AppContextCoroutineScope(mainContext: CoroutineContext):BaseModel(mainContext)

/**
 * Log statement to Crashlytics
 */
fun clLog(s: String) {
    AppContext.clLogCallback(s)
}