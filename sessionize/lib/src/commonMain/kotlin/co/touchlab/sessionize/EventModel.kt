package co.touchlab.sessionize

import co.touchlab.droidcon.db.MySessions
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.api.notificationFeedbackTag
import co.touchlab.sessionize.api.notificationReminderTag
import co.touchlab.sessionize.db.SessionizeDbHelper.sessionQueries
import co.touchlab.sessionize.db.SessionizeDbHelper.userAccountQueries
import co.touchlab.sessionize.db.room
import co.touchlab.sessionize.platform.DateFormatHelper
import co.touchlab.sessionize.platform.NotificationsModel.reminderNotificationsEnabled
import co.touchlab.sessionize.platform.backgroundSuspend
import co.touchlab.sessionize.platform.currentTimeMillis
import co.touchlab.sessionize.platform.logException
import kotlinx.coroutines.launch
import kotlin.math.max

class EventModel(val sessionId: String) : BaseQueryModelView<Session, SessionInfo>(
        sessionQueries.sessionById(sessionId),
        { q ->
            val session = q.executeAsOne()
            collectSessionInfo(session)
        },
        ServiceRegistry.coroutinesDispatcher) {

    init {
        ServiceRegistry.clLogCallback("init EventModel($sessionId)")
    }

    interface EventView : View<SessionInfo>

    private val analyticsDateFormat = DateFormatHelper("MM_dd_HH_mm")
    fun toggleRsvp(event: SessionInfo) = launch {
        toggleRsvpSuspend(event)
    }

    internal suspend fun toggleRsvpSuspend(event: SessionInfo) {
        val rsvp = !event.isRsvped()
        val localSessionId = sessionId

        backgroundSuspend {
            sessionQueries.updateRsvp(if (rsvp) {
                1
            } else {
                0
            }, localSessionId)
        }

        val methodName = if (rsvp) {
            "sessionizeRsvpEvent"
        } else {
            "sessionizeUnrsvpEvent"
        }

        if (rsvp) {
            if (reminderNotificationsEnabled()) {
                ServiceRegistry.notificationsApi.createLocalNotification("Upcoming Event in " + event.session.room().name,
                        event.session.title + " is starting soon.",
                        event.session.startsAt.toLongMillis() + Durations.TEN_MINS_MILLIS,
                        sessionId.toInt(),
                        notificationReminderTag)

                // Feedback Notifications
                if (event.session.feedbackRating == null) {
                    val feedbackNotificationTime = event.session.endsAt.toLongMillis() + Durations.TEN_MINS_MILLIS
                    ServiceRegistry.notificationsApi.createLocalNotification("How was the session?",
                            " Leave feedback for " + event.session.title,
                            feedbackNotificationTime,
                            sessionId.toInt(),
                            notificationFeedbackTag)
                }
            } else {
                ServiceRegistry.notificationsApi.cancelLocalNotification(sessionId.toInt(), notificationReminderTag)
                ServiceRegistry.notificationsApi.cancelLocalNotification(sessionId.toInt(), notificationFeedbackTag)
            }

            ServiceRegistry.sessionizeApi.recordRsvp(methodName, localSessionId)

            sendAnalytics(localSessionId, rsvp)
        }
    }

    private suspend fun sendAnalytics(sessionId: String, rsvp: Boolean) {

        try {
            val session = backgroundSuspend {
                sessionQueries.sessionById(sessionId).executeAsOne()
            }

            val params = HashMap<String, Any>()
            params["slot"] = analyticsDateFormat.format(session.startsAt)
            params["sessionId"] = sessionId
            params["count"] = if (rsvp) {
                1
            } else {
                -1
            }
            ServiceRegistry.analyticsApi.logEvent("RSVP_EVENT", params)
        } catch (e: Exception) {
            logException(e)
        }
    }
}

internal fun collectSessionInfo(session: Session): SessionInfo {
    val speakers = userAccountQueries.selectBySession(session.id).executeAsList()
    val mySessions = sessionQueries.mySessions().executeAsList()

    return SessionInfo(session, speakers, session.conflict(mySessions))
}

internal fun Session.conflict(others: List<MySessions>): Boolean {
    if (this.rsvp == 0L)
        return false

    val now = currentTimeMillis()
    if (now <= this.endsAt.toLongMillis()) {
        for (other in others.filter {
            now <= it.endsAt.toLongMillis() &&
                    this.id != it.id
        }) {
            if (this.startsAt.toLongMillis() < other.endsAt.toLongMillis() &&
                    this.endsAt.toLongMillis() > other.startsAt.toLongMillis())
                return true
        }
    }

    return false
}

data class SessionInfo(
        val session: Session,
        val speakers: List<UserAccount>,
        val conflict: Boolean
)

fun SessionInfo.isNow(): Boolean {
    val now = currentTimeMillis()
    return now < this.session.endsAt.toLongMillis() &&
            now > this.session.startsAt.toLongMillis()
}

fun SessionInfo.isPast(): Boolean {
    return currentTimeMillis() > this.session.endsAt.toLongMillis()
}

fun SessionInfo.isRsvped(): Boolean {
    return this.session.rsvp != 0L
}

suspend fun Session.formattedRoomTime(): String {
    var formattedStart = SessionInfoStuff.roomNameTimeFormatter.format(this.startsAt)
    val formattedEnd = SessionInfoStuff.roomNameTimeFormatter.format(this.endsAt)

    val startMarker = formattedStart.substring(max(formattedStart.length - 3, 0))
    val endMarker = formattedEnd.substring(max(formattedEnd.length - 3, 0))
    if (startMarker == endMarker) {
        formattedStart = formattedStart.substring(0, max(formattedStart.length - 3, 0))
    }

    return "${this.room().name}, $formattedStart - $formattedEnd"
}

object SessionInfoStuff {
    private val TIME_FORMAT = "h:mm a"
    val roomNameTimeFormatter = DateFormatHelper(TIME_FORMAT)
}
