package co.touchlab.notepad

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.multiplatform.architecture.threads.MutableLiveData
import co.touchlab.multiplatform.architecture.threads.map
import co.touchlab.notepad.AppContext.dbHelper
import co.touchlab.notepad.db.QueryLiveData
import co.touchlab.notepad.db.isBlock
import co.touchlab.notepad.db.isRsvp
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

    fun weaveSessionDetailsUi(hourBlock:HourBlock, allBlocks:List<HourBlock>, row:EventRow, allEvents: Boolean){
        val isFirstInBlock = !hourBlock.hourStringDisplay.isEmpty()
        row.setTimeGap(isFirstInBlock)

        row.setTitleText(hourBlock.timeBlock.title)
        row.setTimeText(hourBlock.hourStringDisplay)
        row.setSpeakerText(hourBlock.timeBlock.allNames)
        row.setDescription(hourBlock.timeBlock.description)

        if (hourBlock.timeBlock.isBlock()) {
            row.setLiveNowVisible(false)
            row.setRsvpState(RsvpState.None)
        } else {
            //TODO: Add live
            row.setLiveNowVisible(false)

            val rsvpShow = allEvents && hourBlock.timeBlock.isRsvp()
            val state = if(rsvpShow){
                if(hourBlock.isPast()) {
                    RsvpState.RsvpPast
                }else{
                    if(hourBlock.isConflict(allBlocks)){
                        RsvpState.Conflict
                    }
                    else{
                        RsvpState.Rsvp
                    }
                }
            }else{
                RsvpState.None
            }
            row.setRsvpState(state)
        }
    }

    private class SessionListLiveData(q: Query<SessionWithRoom>) : QueryLiveData<SessionWithRoom, List<SessionWithRoom>>(q), Query.Listener{
        override fun extractData(q: Query<SessionWithRoom>): List<SessionWithRoom> = q.executeAsList()
    }
}

enum class RsvpState {
    None, Rsvp, Conflict, RsvpPast
}

interface EventRow {
    fun setTimeGap(b: Boolean)

    fun setTitleText(s: String)

    fun setTimeText(s: String)

    fun setSpeakerText(s: String)

    fun setDescription(s: String)

    fun setLiveNowVisible(b: Boolean)

    fun setRsvpState(state:RsvpState)
}