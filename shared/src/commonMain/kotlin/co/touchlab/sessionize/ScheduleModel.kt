package co.touchlab.sessionize

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.display.DaySchedule
import co.touchlab.sessionize.display.convertMapToDaySchedule
import co.touchlab.sessionize.display.formatHourBlocks

/**
 * Data model for schedule. Configure live data instances.
 */
class ScheduleModel(private val allEvents: Boolean, sessionizeDbHelper: SessionizeDbHelper, timeZone: String) : BaseQueryModelView<SessionWithRoom, List<DaySchedule>>(
        sessionizeDbHelper.getSessionsQuery(),
        {
            val dbSessions = it.executeAsList()
            val sessions = if (allEvents) {
                dbSessions
            } else {
                dbSessions.filter { it.rsvp != 0L }
            }

            val hourBlocks = formatHourBlocks(sessions, timeZone)
            convertMapToDaySchedule(hourBlocks)
        }) {

    init {
        clLogCallback("init ScheduleModel()")
    }

    fun register(view: ScheduleView) {
        super.register(view)
    }

    interface ScheduleView : View<List<DaySchedule>>
}