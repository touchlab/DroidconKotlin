package co.touchlab.notepad.display

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.notepad.db.getStartLong
import co.touchlab.notepad.db.isBlock

data class HourBlock(
    val hourStringDisplay: String,
    val timeBlock: SessionWithRoom
)

fun sortSessions(sessions:List<SessionWithRoom>):List<SessionWithRoom>{
    val copyList = ArrayList(sessions)
    copyList.sortWith(Comparator { a, b ->  sortTimeBlocks(a, b)})
    return copyList
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