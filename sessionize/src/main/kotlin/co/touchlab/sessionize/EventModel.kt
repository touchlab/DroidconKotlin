package co.touchlab.sessionize

import co.touchlab.droidcon.db.MySessions
import co.touchlab.droidcon.db.SessionWithRoomById
import co.touchlab.droidcon.db.SpeakerForSession
import co.touchlab.sessionize.db.QueryLiveData
import co.touchlab.sessionize.platform.*
import com.squareup.sqldelight.Query
import kotlin.math.max

class EventModel(val sessionId: String) {

    val evenLiveData:EventLiveData

    init {
        val query = goFreeze(AppContext.dbHelper.queryWrapper.sessionQueries.sessionWithRoomById(sessionId))
        evenLiveData = EventLiveData(query)
    }

    fun shutDown(){
        evenLiveData.removeListener()
    }

    private val analyticsDateFormat = DateFormatHelper("MM_dd_HH_mm")
    fun toggleRsvp(rsvp:Boolean){

        networkBackgroundTask {
            val methodName = if(rsvp){"sessionizeRsvpEvent"}else{"sessionizeUnrsvpEvent"}
            val callingUrl = "https://droidcon-server.herokuapp.com/dataTest/$methodName/$sessionId/${AppContext.userUuid()}"
            println("CALLING: $callingUrl")
            simpleGet(callingUrl)
        }

        backgroundTask({
            AppContext.dbHelper.queryWrapper.sessionQueries.sessionById(sessionId).executeAsOne()
        }){
            val params = HashMap<String, Any>()
            params.put("slot", analyticsDateFormat.format(it.startsAt))
            params.put("sessionId", sessionId)
            params.put("count", if(rsvp){1}else{-1})
            AppContext.logEvent("RSVP_EVENT",
                    params)
        }
        backgroundTask {
            AppContext.dbHelper.queryWrapper.sessionQueries.updateRsvp(if(rsvp){1}else{0}, sessionId)
        }
    }

    class EventLiveData(q: Query<SessionWithRoomById>) : QueryLiveData<SessionWithRoomById, SessionInfo>(q, false),
            Query.Listener {
        override fun extractData(q: Query<SessionWithRoomById>): SessionInfo {
            val sessionStuff = q.executeAsOne()
            val speakers = AppContext.dbHelper.queryWrapper.sessionSpeakerQueries.speakerForSession(sessionStuff.id).executeAsList()
            val mySessions = AppContext.dbHelper.queryWrapper.sessionQueries.mySessions().executeAsList()

            return SessionInfo(sessionStuff, speakers, conflict(sessionStuff, mySessions))
        }

        private fun conflict(session:SessionWithRoomById, others: List<MySessions>): Boolean {
            if(session.rsvp == 0L)
                return false

            val now = currentTimeMillis()
            if (now <= session.endsAt.toLongMillis()) {
                for (other in others.filter {
                    now <= it.endsAt.toLongMillis() &&
                            session.id != it.id
                }) {
                    if (session.startsAt.toLongMillis() < other.endsAt.toLongMillis() &&
                            session.endsAt.toLongMillis() > other.startsAt.toLongMillis())
                        return true
                }
            }

            return false
        }
    }
}

data class SessionInfo(
        val session: SessionWithRoomById,
        val speakers: List<SpeakerForSession>,
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

fun SessionInfo.formattedRoomTime():String {
    var formattedStart = SessionInfoStuff.roomNameTimeFormatter.format(this.session.startsAt)
    val formattedEnd = SessionInfoStuff.roomNameTimeFormatter.format(this.session.endsAt)

    val startMarker = formattedStart.substring(max(formattedStart.length - 3, 0))
    val endMarker = formattedEnd.substring(max(formattedEnd.length - 3, 0))
    if (startMarker == endMarker) {
        formattedStart = formattedStart.substring(0, max(formattedStart.length - 3, 0))
    }

    return "${this.session.roomName}, $formattedStart - $formattedEnd"
}

object SessionInfoStuff{
    private val TIME_FORMAT = "h:mm a"
    val roomNameTimeFormatter = DateFormatHelper(TIME_FORMAT)
}