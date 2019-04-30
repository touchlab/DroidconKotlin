package co.touchlab.sessionize

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.sessionize.AppContext.dbHelper
import co.touchlab.sessionize.db.isBlock
import co.touchlab.sessionize.db.isRsvp
import co.touchlab.sessionize.display.DaySchedule
import co.touchlab.sessionize.display.HourBlock
import co.touchlab.sessionize.display.convertMapToDaySchedule
import co.touchlab.sessionize.display.formatHourBlocks
import co.touchlab.sessionize.display.isConflict
import co.touchlab.sessionize.display.isPast
import co.touchlab.stately.ensureNeverFrozen
import co.touchlab.stately.freeze

/**
 * Data model for schedule. Configure live data instances.
 */
class ScheduleModel(private val allEvents: Boolean) : BaseQueryModelView<SessionWithRoom, List<DaySchedule>>(
        dbHelper.getSessionsQuery(),
        {
            val dbSessions = it.executeAsList()
            val sessions = if (allEvents) {
                dbSessions
            } else {
                dbSessions.filter { it.rsvp != 0L }
            }

            val hourBlocks = formatHourBlocks(sessions)
            convertMapToDaySchedule(hourBlocks).freeze() //TODO: This shouldn't need to be frozen
            //Spent several full days trying to debug why, but haven't sorted it out.
        },
        ServiceRegistry.coroutinesDispatcher) {

    init {
        clLog("init ScheduleModel()")
        ensureNeverFrozen()
    }

    fun register(view: ScheduleView) {
        super.register(view)
    }

    interface ScheduleView : View<List<DaySchedule>>

    fun weaveSessionDetailsUi(hourBlock: HourBlock, allBlocks: List<HourBlock>, row: EventRow, allEvents: Boolean) {
        val isFirstInBlock = !hourBlock.hourStringDisplay.isEmpty()
        row.setTimeGap(isFirstInBlock)

        row.setTitleText(hourBlock.timeBlock.title)
        row.setTimeText(hourBlock.hourStringDisplay)
        val speakerNames = if (hourBlock.timeBlock.allNames.isNullOrBlank()) {
            ""
        } else {
            hourBlock.timeBlock.allNames!!
        }
        row.setSpeakerText(speakerNames)
        row.setDescription(hourBlock.timeBlock.description)

        if (hourBlock.timeBlock.isBlock()) {
            row.setLiveNowVisible(false)
            row.setRsvpState(RsvpState.None)
        } else {
            //TODO: Add live
            row.setLiveNowVisible(false)

            val rsvpShow = allEvents && hourBlock.timeBlock.isRsvp()
            val state = if (rsvpShow) {
                if (hourBlock.isPast()) {
                    RsvpState.RsvpPast
                } else {
                    if (hourBlock.isConflict(allBlocks)) {
                        RsvpState.Conflict
                    } else {
                        RsvpState.Rsvp
                    }
                }
            } else {
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

    fun setRsvpState(state: RsvpState)
}