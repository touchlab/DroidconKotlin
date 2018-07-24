package co.touchlab.notepad.schedule

import co.touchlab.notepad.display.HourBlock
import co.touchlab.notepad.db.isBlock
import co.touchlab.notepad.db.isRsvp
import co.touchlab.notepad.display.isConflict
import co.touchlab.notepad.display.isPast

object EventUtils {
    fun styleEventRow(scheduleBlockHour: HourBlock, dataSet: List<HourBlock>, row: EventRow, allEvents: Boolean) {
        val isFirstInBlock = !scheduleBlockHour.hourStringDisplay.isEmpty()
        row.setTimeGap(isFirstInBlock)

        row.setTitleText(scheduleBlockHour.timeBlock.title)
        row.setTimeText(scheduleBlockHour.hourStringDisplay)
        row.setSpeakerText(scheduleBlockHour.timeBlock.allNames)
        row.setDescription(scheduleBlockHour.timeBlock.description)

        if (scheduleBlockHour.timeBlock.isBlock()) {
            row.setLiveNowVisible(false)
            row.setRsvpVisible(false, false)
            row.setRsvpConflict(false)
        } else {
            //TODO: Add live
            row.setLiveNowVisible(false)
            row.setRsvpVisible(allEvents && scheduleBlockHour.timeBlock.isRsvp(), scheduleBlockHour.isPast())
            row.setRsvpConflict(allEvents && scheduleBlockHour.isConflict(dataSet))
        }
    }

    interface EventRow {
        fun setTimeGap(b: Boolean)

        fun setTitleText(s: String)

        fun setTimeText(s: String)

        fun setSpeakerText(s: String)

        fun setDescription(s: String)

        fun setLiveNowVisible(b: Boolean)

        fun setRsvpVisible(rsvp: Boolean, past: Boolean)

        fun setRsvpConflict(b: Boolean)
    }
}