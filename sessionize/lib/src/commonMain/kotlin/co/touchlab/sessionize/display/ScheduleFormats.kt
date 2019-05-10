package co.touchlab.sessionize.display

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.db.isBlock
import co.touchlab.sessionize.db.isRsvp
import co.touchlab.sessionize.platform.DateFormatHelper
import co.touchlab.sessionize.platform.currentTimeMillis
import kotlin.native.concurrent.ThreadLocal

data class DaySchedule(
        val dayString: String,
        val hourBlock: List<HourBlock>
)

data class HourBlock(
        //Need to set this after sorting, which needs dates
        var hourStringDisplay: String,
        val timeBlock: SessionWithRoom,
        val startDateLong: Long,
        val endDateLong: Long,
        val timeGap: Boolean,
        val speakerText: String
){
    fun rowType(): RowType = if (this.timeBlock.isBlock()) {
        RowType.Block
    } else {
        if (this.isPast())
            RowType.PastEvent
        else
            RowType.FutureEvent
    }

    fun isPast(): Boolean = currentTimeMillis() > endDateLong

    fun isConflict(others: List<HourBlock>): Boolean {
        if (this.timeBlock.isRsvp() && !this.isPast()) {
            for (other in others.filter {
                it.timeBlock.isRsvp() &&
                        !it.isPast() &&
                        this.timeBlock.id != it.timeBlock.id
            }) {
                if (this.startDateLong < other.endDateLong &&
                        this.endDateLong > other.startDateLong)
                    return true
            }
        }

        return false
    }

    fun getRsvpState(allEvents: Boolean, allBlocks: List<HourBlock>): RsvpState {
        return if (timeBlock.isBlock()) {
            RsvpState.None
        } else {
            val rsvpShow = allEvents && timeBlock.isRsvp()
            if (rsvpShow) {
                if (isPast()) {
                    RsvpState.RsvpPast
                } else {
                    if (isConflict(allBlocks)) {
                        RsvpState.Conflict
                    } else {
                        RsvpState.Rsvp
                    }
                }
            } else {
                RsvpState.None
            }
        }
    }
}

enum class RsvpState {
    None, Rsvp, Conflict, RsvpPast
}

enum class RowType {
    Block, FutureEvent, PastEvent
}

fun sortSessions(sessions: List<SessionWithRoom>): List<SessionWithRoom> {
    val copyList = ArrayList(sessions)
    copyList.sortWith(Comparator { a, b -> sortTimeBlocks(a, b) })
    return copyList
}

fun sortTimeBlocks(o1: SessionWithRoom, o2: SessionWithRoom): Int {
    val compTimes = o1.startsAt.toLongMillis() - o2.startsAt.toLongMillis()
    if (compTimes != 0L) {
        return if (compTimes > 0) 1 else -1
    }

    if (o1.isBlock() && o2.isBlock()) {
        return 0
    }

    if (o1.isBlock()) {
        return 1
    }
    return if (o2.isBlock()) {
        -1
    } else o1.roomName.compareTo(o2.roomName)
}

fun formatHourBlocks(inList: List<SessionWithRoom>): HashMap<String, ArrayList<HourBlock>> {

    val eventAndBlockList = sortSessions(inList)

    val dateWithBlocksTreeMap = HashMap<String, ArrayList<HourBlock>>()
    var lastHourDisplay = ""

    for (timeBlock in eventAndBlockList) {
        val startDateObj = timeBlock.startsAt
        val TAB_DATE_FORMAT = DateFormatHelper("MMM dd")
        val startDate = TAB_DATE_FORMAT.formatConferenceTZ(startDateObj)
        var blockHourList: ArrayList<HourBlock>? = dateWithBlocksTreeMap.get(startDate)
        if (blockHourList == null) {
            blockHourList = ArrayList()
            dateWithBlocksTreeMap[startDate] = blockHourList
        }

        val TIME_FORMAT = DateFormatHelper("h:mma")
        val startTime = TIME_FORMAT.formatConferenceTZ(startDateObj)
        val newHourDisplay = lastHourDisplay != startTime

        blockHourList.add(HourBlock(
                hourStringDisplay = if (newHourDisplay) startTime else "",
                timeBlock = timeBlock,
                startDateLong = timeBlock.startsAt.toLongMillis(),
                endDateLong = timeBlock.endsAt.toLongMillis(),
                timeGap = newHourDisplay,
                speakerText = timeBlock.allNames.orEmpty()))
        lastHourDisplay = startTime
    }
    return dateWithBlocksTreeMap
}

fun convertMapToDaySchedule(dateWithBlocksTreeMap: HashMap<String, ArrayList<HourBlock>>): List<DaySchedule> {
    val dayScheduleList = ArrayList<DaySchedule>()

    for (dateString in dateWithBlocksTreeMap.keys) {
        val hourBlocksMap = dateWithBlocksTreeMap.get(dateString)
        val daySchedule = DaySchedule(dateString,
                hourBlocksMap!!)
        dayScheduleList.add(daySchedule)
    }

    dayScheduleList.sortWith(Comparator { a, b ->
        a.hourBlock[0].startDateLong.compareTo(b.hourBlock[0].startDateLong)
    })

    return dayScheduleList
}
