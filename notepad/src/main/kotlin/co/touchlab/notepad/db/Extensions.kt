package co.touchlab.notepad.db

import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.multiplatform.architecture.threads.MediatorLiveData
import co.touchlab.multiplatform.architecture.threads.MutableLiveData
import co.touchlab.multiplatform.architecture.threads.Observer
import co.touchlab.notepad.utils.Date
import co.touchlab.notepad.utils.DateFormatHelper
import co.touchlab.notepad.utils.SESSIONIZE_DATE_FORMAT

val formatter = DateFormatHelper(SESSIONIZE_DATE_FORMAT)

fun SessionWithRoom.getStart():Date = formatter.toDate(this.startsAt)
fun SessionWithRoom.getStartLong():Long = this.getStart().toLongMillis()

fun SessionWithRoom.getEnd():Date = formatter.toDate(this.startsAt)
fun SessionWithRoom.getEndLong():Long = this.getEnd().toLongMillis()

fun SessionWithRoom.isBlock():Boolean = this.serviceSession != 0L

