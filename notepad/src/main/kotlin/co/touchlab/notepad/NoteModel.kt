package co.touchlab.notepad

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.notepad.db.NoteDbHelper
import co.touchlab.notepad.sqldelight.Note
import co.touchlab.notepad.utils.backgroundTask
import co.touchlab.notepad.utils.currentTimeMillis
import co.touchlab.multiplatform.architecture.threads.*
import co.touchlab.notepad.db.QueryLiveData
import co.touchlab.notepad.display.DaySchedule
import co.touchlab.notepad.display.convertMapToDaySchedule
import co.touchlab.notepad.display.formatHourBlocks
import co.touchlab.notepad.display.sortSessions
import com.squareup.sqldelight.Query

class NoteModel {

    companion object {
        val dbHelper = NoteDbHelper()
    }

    val liveData:ListLiveData
    val liveSessions:SessionListLiveData

    init {
        val query = dbHelper.getSpeakers()
        liveData = ListLiveData(query)
        query.addListener(liveData)

        val sessionQuery = dbHelper.getSessionsQuery()
        liveSessions = SessionListLiveData(sessionQuery)
        sessionQuery.addListener(liveSessions)
    }

    fun shutDown(){
        dbHelper.getSpeakers().removeListener(liveData)
        dbHelper.getSessionsQuery().removeListener(liveSessions)
    }

    fun notesLiveData():MutableLiveData<List<UserAccount>> = liveData
    fun sessionsLiveData():MutableLiveData<List<SessionWithRoom>> = liveSessions

    fun primeData(speakerJson:String, scheduleJson:String){
        backgroundTask {
            dbHelper.primeAll(speakerJson, scheduleJson)
        }
    }

    fun insertNote(title: String, description: String) {
        backgroundTask {
            val now = currentTimeMillis()
            /*dbHelper.insertNotes(
                    Array(1) {
                        Note.Impl(
                                title = title,
                                note = description,
                                created = now,
                                modified = now,
                                hiblob = null,
                                id = Long.MIN_VALUE)
                    })*/
        }
    }

    class ListLiveData(q: Query<UserAccount>) : QueryLiveData<UserAccount, List<UserAccount>>(q), Query.Listener {
        override fun extractData(q: Query<*>): List<UserAccount>  = q.executeAsList() as List<UserAccount>
    }

    fun dayFormatLiveData():MediatorLiveData<List<DaySchedule>>
    {
        val liveDataMerger = MediatorLiveData<List<DaySchedule>>()

        liveDataMerger.addSource(liveSessions) {
            val dayschedules = convertMapToDaySchedule(formatHourBlocks(it))

            liveDataMerger.setValue(dayschedules)
        }

        return liveDataMerger
    }

    class SessionListLiveData(q: Query<SessionWithRoom>) : QueryLiveData<SessionWithRoom, List<SessionWithRoom>>(q), Query.Listener{
        override fun extractData(q: Query<*>): List<SessionWithRoom> {
            val sessions = q.executeAsList() as List<SessionWithRoom>
            for (session in sessions) {
                println("YYYYYYYYY: ${session.id}")
            }
            val sorted = sortSessions(sessions)
            return sorted
        }
    }
}