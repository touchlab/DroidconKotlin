package co.touchlab.sessionize

import co.touchlab.droidcon.db.RoomQueries
import co.touchlab.droidcon.db.SessionQueries
import co.touchlab.droidcon.db.SponsorQueries
import co.touchlab.droidcon.db.UserAccountQueries
import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.SessionizeApiImpl
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.platform.*
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.freeze
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object AppContext {

    val dbHelper = SessionizeDbHelper()

    val KEY_FIRST_RUN = "FIRST_RUN1"
    val KEY_LAST_LOAD = "LAST_LOAD"
    val USER_UUID = "USER_UUID"
    val TWO_HOURS_MILLIS = 2 * 60 * 60 * 1000

    val lambdas = AtomicReference<PlatformLambdas?>(null)

    fun initAppContext(staticFileLoader: (filePrefix: String, fileType: String) -> String?,
            clLogCallback: (s: String) -> Unit) {

        dbHelper.initDatabase(ServiceRegistry.dbDriver)

        lambdas.value = PlatformLambdas(
                staticFileLoader,
                clLogCallback).freeze()
    }

    internal val sessionQueries: SessionQueries
        get() = AppContext.dbHelper.instance.sessionQueries

    internal val userAccountQueries: UserAccountQueries
        get() = AppContext.dbHelper.instance.userAccountQueries

    internal val roomQueries: RoomQueries
        get() = AppContext.dbHelper.instance.roomQueries

    internal val sponsorQueries: SponsorQueries
        get() = AppContext.dbHelper.instance.sponsorQueries

    data class PlatformLambdas(val staticFileLoader: (filePrefix: String, fileType: String) -> String?,
                               val clLogCallback: (s: String) -> Unit)

    val staticFileLoader: (filePrefix: String, fileType: String) -> String?
        get() = lambdas.value!!.staticFileLoader

    val clLogCallback: (s: String) -> Unit
        get() = lambdas.value!!.clLogCallback

    private fun firstRun(): Boolean = ServiceRegistry.appSettings.getBoolean(KEY_FIRST_RUN, true)

    private fun updateFirstRun() {
        ServiceRegistry.appSettings.putBoolean(KEY_FIRST_RUN, false)
    }

    fun userUuid(): String {
        if (ServiceRegistry.appSettings.getString(USER_UUID).isBlank()) {
            ServiceRegistry.appSettings.putString(USER_UUID, createUuid())
        }
        return ServiceRegistry.appSettings.getString(USER_UUID)
    }

    //Split these up so they can individually succeed/fail
    fun dataLoad() {
        if (firstRun()) {
            backgroundTask({
                try {
                    seedFileLoad()
                } catch (e: Exception) {
                    logException(e)
                }
            }) {
                refreshData()
            }
        }
    }

    internal fun seedFileLoad() {
        if (firstRun()) {
            val staticFileLoader = lambdas.value!!.staticFileLoader
            val sponsorJson = staticFileLoader("sponsors", "json")
            val speakerJson = staticFileLoader("speakers", "json")
            val scheduleJson = staticFileLoader("schedule", "json")

            if (sponsorJson != null && speakerJson != null && scheduleJson != null) {
                dbHelper.primeAll(speakerJson, scheduleJson, sponsorJson)
                updateFirstRun()
            } else {
                //This should only ever happen in dev
                throw NullPointerException("Couldn't load static files")
            }
        }
    }

    private fun dataCalls() = AppContextCoroutineScope(ServiceRegistry.coroutinesDispatcher).launch {
        try {
            val api = ServiceRegistry.sessionizeApi
            val networkSpeakerJson = api.getSpeakersJson()
            val networkSessionJson = api.getSessionsJson()
            val networkSponsorJson = api.getSponsorJson()

            backgroundSuspend {
                dbHelper.primeAll(networkSpeakerJson, networkSessionJson, networkSponsorJson)
                ServiceRegistry.appSettings.putLong(KEY_LAST_LOAD, currentTimeMillis())
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    fun refreshData() {
        if (!firstRun()) {
            val lastLoad = ServiceRegistry.appSettings.getLong(KEY_LAST_LOAD)
            if (lastLoad < (currentTimeMillis() - (TWO_HOURS_MILLIS.toLong()))) {
                dataCalls()
            }
        }
    }
}

val <T> ThreadLocalRef<T>.lateValue: T
    get() = this.value!!

internal class AppContextCoroutineScope(mainContext: CoroutineContext) : BaseModel(mainContext)

/**
 * Log statement to Crashlytics
 */
fun clLog(s: String) {
    AppContext.clLogCallback(s)
}