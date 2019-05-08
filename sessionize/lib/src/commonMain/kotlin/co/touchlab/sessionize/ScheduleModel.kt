package co.touchlab.sessionize

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.db.isBlock
import co.touchlab.sessionize.db.isRsvp
import co.touchlab.sessionize.display.DaySchedule
import co.touchlab.sessionize.display.convertMapToDaySchedule
import co.touchlab.sessionize.display.formatHourBlocks
import co.touchlab.stately.ensureNeverFrozen
import co.touchlab.stately.freeze

/**
 * Data model for schedule. Configure live data instances.
 */
class ScheduleModel(private val allEvents: Boolean) : BaseQueryModelView<SessionWithRoom, List<DaySchedule>>(
        SessionizeDbHelper.getSessionsQuery(),
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
        ServiceRegistry.clLogCallback("init ScheduleModel()")
        ensureNeverFrozen()
    }

    fun register(view: ScheduleView) {
        super.register(view)
    }

    interface ScheduleView : View<List<DaySchedule>>
}