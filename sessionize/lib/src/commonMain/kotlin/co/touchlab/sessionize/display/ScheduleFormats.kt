package co.touchlab.sessionize.display

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.sessionize.db.isBlock
import co.touchlab.sessionize.db.isRsvp
import co.touchlab.sessionize.platform.DateFormatHelper
import co.touchlab.sessionize.platform.currentTimeMillis
import co.touchlab.stately.annotation.ThreadLocal

data class DaySchedule(
        val dayString: String,
        val hourBlock: List<HourBlock>
)

data class HourBlock(
        //Need to set this after sorting, which needs dates
        var hourStringDisplay: String,
        val timeBlock: SessionWithRoom,
        val startDateLong: Long = timeBlock.startsAt.toLongMillis(),
        val endDateLong: Long = timeBlock.endsAt.toLongMillis()
)

enum class RowType {
    Block, FutureEvent, PastEvent
}

fun HourBlock.rowType(): RowType = if (this.timeBlock.isBlock()) {
    RowType.Block
} else {
    if (this.isPast())
        RowType.PastEvent
    else
        RowType.FutureEvent
}

fun HourBlock.isPast(): Boolean = currentTimeMillis() > endDateLong

fun HourBlock.isConflict(others: List<HourBlock>): Boolean {
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

@ThreadLocal
val TAB_DATE_FORMAT = DateFormatHelper("MMM dd")
@ThreadLocal
val TIME_FORMAT = DateFormatHelper("h:mma")

fun formatHourBlocks(inList: List<SessionWithRoom>): HashMap<String, ArrayList<HourBlock>> {

    val eventAndBlockList = sortSessions(inList)

    val dateWithBlocksTreeMap = HashMap<String, ArrayList<HourBlock>>()
    var lastHourDisplay = ""

    for (timeBlock in eventAndBlockList) {
        val startDateObj = timeBlock.startsAt
        val startDate = TAB_DATE_FORMAT.format(startDateObj)
        var blockHourList: ArrayList<HourBlock>? = dateWithBlocksTreeMap.get(startDate)
        if (blockHourList == null) {
            blockHourList = ArrayList()
            dateWithBlocksTreeMap.put(startDate, blockHourList)
        }

        val startTime = TIME_FORMAT.format(startDateObj)
        val newHourDisplay = lastHourDisplay != startTime
        blockHourList.add(HourBlock(if (newHourDisplay) startTime else "", timeBlock))
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

