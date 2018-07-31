package co.touchlab.sessionize.db

import co.touchlab.droidcon.db.SessionWithRoom

fun SessionWithRoom.isBlock():Boolean = this.serviceSession != 0L
fun SessionWithRoom.isRsvp():Boolean = this.rsvp != 0L

