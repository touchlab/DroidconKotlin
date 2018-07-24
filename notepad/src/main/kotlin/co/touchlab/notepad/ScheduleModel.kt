package co.touchlab.notepad

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.multiplatform.architecture.threads.MutableLiveData
import co.touchlab.multiplatform.architecture.threads.map
import co.touchlab.notepad.AppContext.dbHelper
import co.touchlab.notepad.db.QueryLiveData
import co.touchlab.notepad.display.*
import com.squareup.sqldelight.Query

/**
 * Data model for schedule. Configure live data instances.
 */
class ScheduleModel {
    private val liveSessions:SessionListLiveData

    init {
        val sessionQuery = dbHelper.getSessionsQuery()
        liveSessions = SessionListLiveData(sessionQuery)
    }

    fun shutDown(){
        liveSessions.removeListener()
    }

    fun isConflict(hourBlock: HourBlock, others:List<HourBlock>) = hourBlock.isConflict(others)

    fun dayFormatLiveData(allEvents:Boolean):MutableLiveData<List<DaySchedule>> {
        return liveSessions.map {
            val sessions = if(allEvents){it}else{it.filter {it.rsvp != 0L}}
            convertMapToDaySchedule(formatHourBlocks(sessions))
        }
    }

    private class SessionListLiveData(q: Query<SessionWithRoom>) : QueryLiveData<SessionWithRoom, List<SessionWithRoom>>(q), Query.Listener{
        override fun extractData(q: Query<*>): List<SessionWithRoom> = q.executeAsList() as List<SessionWithRoom>
    }
}

