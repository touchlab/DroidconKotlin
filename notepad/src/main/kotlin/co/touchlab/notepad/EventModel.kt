package co.touchlab.notepad

import co.touchlab.droidcon.db.SessionWithRoomById
import co.touchlab.droidcon.db.SpeakerForSession
import co.touchlab.multiplatform.architecture.threads.MediatorLiveData
import co.touchlab.multiplatform.architecture.threads.addSource
import co.touchlab.notepad.db.QueryLiveData
import co.touchlab.notepad.utils.currentTimeMillis
import com.squareup.sqldelight.Query

class EventModel(val sessionId: String) {

    val sessionInfoLiveData = MediatorLiveData<SessionInfo>()
    var sessionData: SessionWithRoomById? = null
    var speakerData: List<SpeakerForSession>? = null
    private val evenLiveData:EventLiveData
    private val speakerForSessionLiveData:SpeakerForSessionLiveData

    init {
        evenLiveData = EventLiveData(AppContext.dbHelper.queryWrapper.sessionQueries.sessionWithRoomById(sessionId))
        sessionInfoLiveData.addSource(
                evenLiveData
        ) {
            sessionData = it
            checkAndPush()
        }

        speakerForSessionLiveData = SpeakerForSessionLiveData(AppContext.dbHelper.queryWrapper.sessionSpeakerQueries.speakerForSession(sessionId))
        sessionInfoLiveData.addSource(
                speakerForSessionLiveData
        ) {
            speakerData = it
            checkAndPush()
        }
    }

    fun shutDown(){
        evenLiveData.removeListener()
        speakerForSessionLiveData.removeListener()
    }

    private fun checkAndPush(){
        if(sessionData != null && speakerData != null)
            sessionInfoLiveData.setValue(SessionInfo(sessionData!!, speakerData!!))
    }

    class EventLiveData(q: Query<SessionWithRoomById>) : QueryLiveData<SessionWithRoomById, SessionWithRoomById>(q), Query.Listener {
        override fun extractData(q: Query<*>): SessionWithRoomById = q.executeAsOne() as SessionWithRoomById
    }

    class SpeakerForSessionLiveData(q: Query<SpeakerForSession>) : QueryLiveData<SpeakerForSession, List<SpeakerForSession>>(q), Query.Listener {
        override fun extractData(q: Query<*>): List<SpeakerForSession> = q.executeAsList() as List<SpeakerForSession>
    }
}

data class SessionInfo(
        val session: SessionWithRoomById,
        val speakers: List<SpeakerForSession>
)

fun SessionInfo.isNow(): Boolean {
    val now = currentTimeMillis()
    return now < this.session.endsAt.toLongMillis() &&
            now > this.session.startsAt.toLongMillis()
}

fun SessionInfo.isPast(): Boolean {
    return currentTimeMillis() > this.session.endsAt.toLongMillis()
}