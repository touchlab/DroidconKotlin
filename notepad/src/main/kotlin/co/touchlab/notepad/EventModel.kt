package co.touchlab.notepad

import co.touchlab.droidcon.db.MySessions
import co.touchlab.droidcon.db.SessionWithRoomById
import co.touchlab.droidcon.db.SpeakerForSession
import co.touchlab.notepad.db.QueryLiveData
import co.touchlab.notepad.utils.backgroundTask
import co.touchlab.notepad.utils.currentTimeMillis
import co.touchlab.notepad.utils.goFreeze
import co.touchlab.notepad.utils.sleepThread
import com.squareup.sqldelight.Query

class EventModel(val sessionId: String) {

    val evenLiveData:EventLiveData

    init {
        val query = goFreeze(AppContext.dbHelper.queryWrapper.sessionQueries.sessionWithRoomById(sessionId))
        evenLiveData = EventLiveData(query)
    }

    fun shutDown(){
        evenLiveData.removeListener()
    }

    fun toggleRsvp(rsvp:Boolean){
        backgroundTask {
            AppContext.dbHelper.queryWrapper.sessionQueries.updateRsvp(if(rsvp){1}else{0}, sessionId)
        }
    }

    class EventLiveData(q: Query<SessionWithRoomById>) : QueryLiveData<SessionWithRoomById, SessionInfo>(q, false),
            Query.Listener {
        override fun extractData(q: Query<*>): SessionInfo {
            val sessionStuff = q.executeAsOne() as SessionWithRoomById
            val speakers = AppContext.dbHelper.queryWrapper.sessionSpeakerQueries.speakerForSession(sessionStuff.id).executeAsList()
            val mySessions = AppContext.dbHelper.queryWrapper.sessionQueries.mySessions().executeAsList() as List<MySessions>

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