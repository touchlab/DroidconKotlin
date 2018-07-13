package co.touchlab.notepad.display

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.notepad.db.getStart
import co.touchlab.notepad.db.getStartLong
import co.touchlab.notepad.db.isBlock
import co.touchlab.notepad.utils.DateFormatHelper

data class DaySchedule(
        val dayString: String,
        val hourBlock: List<HourBlock>
)

data class HourBlock(
        val hourStringDisplay: String,
        val timeBlock: SessionWithRoom
)

fun sortSessions(sessions:List<SessionWithRoom>):List<SessionWithRoom>{
    val copyList = ArrayList(sessions)
    copyList.sortWith(Comparator { a, b ->  sortTimeBlocks(a, b)})
    return copyList
}

val DATE_FORMAT = DateFormatHelper("MM/dd/yyyy")
val TIME_FORMAT = DateFormatHelper("h:mma")

fun formatHourBlocks(eventAndBlockList: List<SessionWithRoom>): HashMap<String, ArrayList<HourBlock>> {
    val dateWithBlocksTreeMap = HashMap<String, ArrayList<HourBlock>>()
    var lastHourDisplay = ""

    for (timeBlock in eventAndBlockList) {
        val startDateObj = timeBlock.getStart()
        val startDate = DATE_FORMAT.format(startDateObj)
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

    dayScheduleList.sortWith(Comparator { a, b -> a.dayString.compareTo(b.dayString) })

    return dayScheduleList
}

fun sortTimeBlocks(o1: SessionWithRoom, o2: SessionWithRoom): Int {
    val compTimes = o1.getStartLong() - o2.getStartLong()
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