package co.touchlab.sessionize

import co.touchlab.droidcon.db.MySessions
import co.touchlab.droidcon.db.RoomQueries
import co.touchlab.droidcon.db.SessionQueries
import co.touchlab.droidcon.db.SponsorQueries
import co.touchlab.droidcon.db.UserAccountQueries
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.platform.NotificationFeedbackTag
import co.touchlab.sessionize.platform.NotificationReminderTag
import co.touchlab.sessionize.platform.backgroundSuspend
import co.touchlab.sessionize.platform.backgroundTask
import co.touchlab.sessionize.platform.createLocalNotification
import co.touchlab.sessionize.platform.createUuid
import co.touchlab.sessionize.platform.currentTimeMillis
import co.touchlab.sessionize.platform.deinitializeNotifications
import co.touchlab.sessionize.platform.initializeNotifications
import co.touchlab.sessionize.platform.logException
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.freeze
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object AppContext {

    //private var feedbackEnabled: Boolean = true

    val dbHelper = SessionizeDbHelper()

    val KEY_FIRST_RUN = "FIRST_RUN1"
    val KEY_LAST_LOAD = "LAST_LOAD"
    val USER_UUID = "USER_UUID"
    val TWO_HOURS_MILLIS = 2 * 60 * 60 * 1000
    val TEN_MINS_MILLIS = 1000 * 10 * 60


    val lambdas = AtomicReference<PlatformLambdas?>(null)

    fun initAppContext(staticFileLoader: (filePrefix: String, fileType: String) -> String?,
            clLogCallback: (s: String) -> Unit) {

        dbHelper.initDatabase(ServiceRegistry.dbDriver)

        lambdas.value = PlatformLambdas(
                staticFileLoader,
                clLogCallback).freeze()
        initializeNotifications()
        //feedbackEnabled = true
    }

    fun deinitPlatformClient(){
        deinitializeNotifications()
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

        //If we do some kind of data re-load after a user logs in, we'll need to update this.
        //We assume for now that when the app first starts, you have nothing rsvp'd
        createNotificationsForSessions()
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

    private fun createNotificationsForSessions() {
        backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
            mySessions.forEach { session ->
                val notificationTime = session.startsAt.toLongMillis() - TEN_MINS_MILLIS
                if (notificationTime > currentTimeMillis()) {
                    createLocalNotification("Upcoming Event in " + session.roomName,
                            session.title + " is starting soon.",
                            notificationTime,
                            session.id.toInt(),
                            NotificationReminderTag)
                }

                // Feedback Notifications
                if(session.feedbackRating == null) {
                    val feedbackNotificationTime = session.endsAt.toLongMillis() + TEN_MINS_MILLIS
                    createLocalNotification("How was the session?",
                            " Leave feedback for " + session.title,
                            feedbackNotificationTime,
                            session.id.toInt(),
                            NotificationFeedbackTag)
                }
            }
        }
    }

    fun requestMySessionsForFeedback(): List<MySessions>{
        return sessionQueries.mySessions().executeAsList().filter {
            it.feedbackRating == null && it.endsAt.toLongMillis() < currentTimeMillis()
        }

    }

    fun disableFeedback(){
        //feedbackEnabled = false
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