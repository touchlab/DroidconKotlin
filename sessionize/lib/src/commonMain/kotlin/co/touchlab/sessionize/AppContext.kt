package co.touchlab.sessionize

import co.touchlab.droidcon.db.MySessions
import co.touchlab.droidcon.db.RoomQueries
import co.touchlab.droidcon.db.SessionQueries
import co.touchlab.droidcon.db.SponsorQueries
import co.touchlab.droidcon.db.UserAccountQueries
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.api.notificationFeedbackTag
import co.touchlab.sessionize.api.notificationReminderTag
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.platform.backgroundSuspend
import co.touchlab.sessionize.platform.backgroundTask
import co.touchlab.sessionize.platform.createUuid
import co.touchlab.sessionize.platform.currentTimeMillis
import co.touchlab.sessionize.platform.logException
import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

object AppContext {

    private val primeJson = Json.nonstrict
    val dbHelper = SessionizeDbHelper()

    val KEY_FIRST_RUN = "FIRST_RUN1"
    val KEY_LAST_LOAD = "LAST_LOAD"
    val USER_UUID = "USER_UUID"
    val TWO_HOURS_MILLIS = 2 * 60 * 60 * 1000
    val TEN_MINS_MILLIS = 1000 * 10 * 60


    fun initAppContext() {
        dbHelper.initDatabase(ServiceRegistry.dbDriver)
    }

    internal val sessionQueries: SessionQueries
        get() = AppContext.dbHelper.instance.sessionQueries

    internal val userAccountQueries: UserAccountQueries
        get() = AppContext.dbHelper.instance.userAccountQueries

    internal val roomQueries: RoomQueries
        get() = AppContext.dbHelper.instance.roomQueries

    internal val sponsorQueries: SponsorQueries
        get() = AppContext.dbHelper.instance.sponsorQueries

    val staticFileLoader: (filePrefix: String, fileType: String) -> String?
        get() = ServiceRegistry.staticFileLoader

    val clLogCallback: (s: String) -> Unit
        get() = ServiceRegistry.clLogCallback

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

    fun loadSponsors(): String? {
        return staticFileLoader("sponsors", "json")
    }

    fun loadSpeakers(): String? {
        return staticFileLoader("speakers", "json")
    }

    fun loadSchedule(): String? {
        return staticFileLoader("schedule", "json")
    }

    fun loadAbout(): String? {
        return staticFileLoader.invoke("about", "json")
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
            val sponsorJson = loadSponsors()
            val speakerJson = loadSpeakers()
            val scheduleJson = loadSchedule()

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
                    ServiceRegistry.notificationsApi.createLocalNotification("Upcoming Event in " + session.roomName,
                            session.title + " is starting soon.",
                            notificationTime,
                            session.id.hashCode(),
                            notificationReminderTag)
                }

                // Feedback Notifications
                if(session.feedbackRating == null) {
                    val feedbackNotificationTime = session.endsAt.toLongMillis() + TEN_MINS_MILLIS
                    ServiceRegistry.notificationsApi.createLocalNotification("How was the session?",
                            " Leave feedback for " + session.title,
                            feedbackNotificationTime,
                            //Not great. Possible to clash, although super unlikely
                            session.id.hashCode(),
                            notificationFeedbackTag)
                }
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
