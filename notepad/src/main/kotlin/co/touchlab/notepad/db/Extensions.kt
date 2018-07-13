package co.touchlab.notepad.db

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.notepad.utils.DateFormatHelper
import co.touchlab.notepad.utils.SESSIONIZE_DATE_FORMAT

val formatter = DateFormatHelper(SESSIONIZE_DATE_FORMAT)

fun SessionWithRoom.getStartLong():Long = formatter.toDate(this.startsAt).toLongMillis()
fun SessionWithRoom.getEndLong():Long = formatter.toDate(this.startsAt).toLongMillis()

fun SessionWithRoom.isBlock():Boolean = this.serviceSession != 0L