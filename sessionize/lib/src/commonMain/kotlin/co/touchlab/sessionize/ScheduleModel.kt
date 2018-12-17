package co.touchlab.sessionize

import co.touchlab.sessionize.AppContext.dbHelper
import co.touchlab.sessionize.db.QueryUpdater
import co.touchlab.sessionize.db.isBlock
import co.touchlab.sessionize.db.isRsvp
import co.touchlab.sessionize.display.*
import co.touchlab.stately.isFrozen

/**
 * Data model for schedule. Configure live data instances.
 */
class ScheduleModel : BaseModel(AppContext.dispatcherLocal.lateValue) {
    private var allEvents = true
    private val sessionsUpdater = QueryUpdater(q = dbHelper.getSessionsQuery(),
            extractData = { it.executeAsList() },
            updateSource = {dbSessions ->
                println("dbSessions frozen ${dbSessions.isFrozen()}")
                scheduleView?.let {
                    val sessions = if(allEvents){dbSessions}else{dbSessions.filter {it.rsvp != 0L}}
                    val daySchedules = convertMapToDaySchedule(formatHourBlocks(sessions))
                    it.update(daySchedules)
                }
            })

    private var scheduleView:ScheduleView? = null

    init {
        clLog("init ScheduleModel()")
    }

    fun register(allEvents: Boolean, view:ScheduleView){
        this.allEvents = allEvents
        scheduleView = view
        sessionsUpdater.refresh()
    }

    fun shutDown(){
        scheduleView = null
        sessionsUpdater.destroy()
    }

    interface ScheduleView{
        fun update(daySchedules:List<DaySchedule>)
    }

    fun weaveSessionDetailsUi(hourBlock:HourBlock, allBlocks:List<HourBlock>, row:EventRow, allEvents: Boolean){
        val isFirstInBlock = !hourBlock.hourStringDisplay.isEmpty()
        row.setTimeGap(isFirstInBlock)

        row.setTitleText(hourBlock.timeBlock.title)
        row.setTimeText(hourBlock.hourStringDisplay)
        val speakerNames = if(hourBlock.timeBlock.allNames.isNullOrBlank()){""}else{hourBlock.timeBlock.allNames!!}
        row.setSpeakerText(speakerNames)
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