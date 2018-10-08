package co.touchlab.sessionize.db

import co.touchlab.droidcon.db.Room
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.AppContext

fun SessionWithRoom.isBlock():Boolean = this.serviceSession != 0L
fun SessionWithRoom.isRsvp():Boolean = this.rsvp != 0L

/**
 * Provide for "ORM-like" associated query
 */
fun UserAccount.sessionsAsync(): List<Session> {
    val id = this.id
    return AppContext.dbHelper.queryWrapper.sessionQueries.userSessions(id).executeAsList()
}

fun Session.roomAsync(): Room {
    val id = this.roomId!!
    return AppContext.dbHelper.queryWrapper.
            roomQueries.selectById(id).executeAsOne()
}