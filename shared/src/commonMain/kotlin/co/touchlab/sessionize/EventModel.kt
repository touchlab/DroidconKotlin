package co.touchlab.sessionize

import co.touchlab.droidcon.db.MySessions
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.db.room
import co.touchlab.sessionize.platform.DateFormatHelper
import co.touchlab.sessionize.platform.NotificationsModel
import co.touchlab.sessionize.platform.currentTimeMillis
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max

class EventModel(
    val sessionId: String,
    val dbHelper: SessionizeDbHelper,
    val sessionizeApi: SessionizeApi,
val notificationsModel: NotificationsModel) : BaseQueryModelView<Session, SessionInfo>(
        dbHelper.sessionQueries.sessionById(sessionId),
        { q ->
            val session = q.executeAsOne()
            collectSessionInfo(session, dbHelper)
        }) {

    init {
        clLogCallback("init EventModel($sessionId)")
    }

    interface EventView : View<SessionInfo>

    fun toggleRsvp(event: SessionInfo) = mainScope.launch {
        toggleRsvpSuspend(event)
    }

    internal suspend fun toggleRsvpSuspend(event: SessionInfo) {
        val rsvp = !event.isRsvped()

        callUpdateRsvp(rsvp, sessionId)

        val methodName = if (rsvp) {
            "sessionizeRsvpEvent"
        } else {
            "sessionizeUnrsvpEvent"
        }

        notificationsModel.recreateReminderNotifications()
        notificationsModel.recreateFeedbackNotifications()
        if (rsvp) {
            sessionizeApi.recordRsvp(methodName, sessionId)

            sendAnalytics(sessionId, rsvp)
        }
    }

    internal suspend fun callUpdateRsvp(rsvp: Boolean, localSessionId: String) = withContext(backgroundDispatcher){
        dbHelper.sessionQueries.updateRsvp(if (rsvp) {
            1
        } else {
            0
        }, localSessionId)
    }

    private suspend fun sendAnalytics(sessionId: String, rsvp: Boolean) = withContext(backgroundDispatcher) {
        try {
            val session = dbHelper.sessionQueries.sessionById(sessionId).executeAsOne()
            val params = HashMap<String, Any>()
            val analyticsDateFormat = DateFormatHelper("MM_dd_HH_mm", timeZone)
            params["slot"] = analyticsDateFormat.formatConferenceTZ(session.startsAt)
            params["sessionId"] = sessionId
            params["count"] = if (rsvp) {
                1
            } else {
                -1
            }
            analyticsApi.logEvent("RSVP_EVENT", params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

internal fun collectSessionInfo(session: Session, dbHelper: SessionizeDbHelper): SessionInfo {
    val speakers = dbHelper.userAccountQueries.selectBySession(session.id).executeAsList()
    val mySessions = dbHelper.sessionQueries.mySessions().executeAsList()

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

suspend fun Session.formattedRoomTime(formatter: SessionRoomtimeFormatter, dbHelper: SessionizeDbHelper): String {
    var formattedStart = formatter.roomNameTimeFormatter.formatConferenceTZ(this.startsAt)
    val formattedEnd = formatter.roomNameTimeFormatter.formatConferenceTZ(this.endsAt)

    val startMarker = formattedStart.substring(max(formattedStart.length - 3, 0))
    val endMarker = formattedEnd.substring(max(formattedEnd.length - 3, 0))
    if (startMarker == endMarker) {
        formattedStart = formattedStart.substring(0, max(formattedStart.length - 3, 0))
    }

    return "${this.room(dbHelper).name}, $formattedStart - $formattedEnd"
}

class SessionRoomtimeFormatter(timeZone: String) {
    private val TIME_FORMAT = "h:mm a"
    val roomNameTimeFormatter = DateFormatHelper(TIME_FORMAT, timeZone)
}
