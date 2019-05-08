package co.touchlab.sessionize.db

import co.touchlab.droidcon.db.Room
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.db.SessionizeDbHelper.roomQueries
import co.touchlab.sessionize.db.SessionizeDbHelper.sessionQueries
import co.touchlab.sessionize.platform.backgroundSuspend

fun SessionWithRoom.isBlock(): Boolean = this.serviceSession != 0L
fun SessionWithRoom.isRsvp(): Boolean = this.rsvp != 0L

/**
 * Provide for "ORM-like" associated query
 */
internal suspend fun UserAccount.sessions(): List<Session> {
    val id = this.id
    return backgroundSuspend { sessionQueries.userSessions(id).executeAsList() }
}

internal suspend fun Session.room(): Room {
    val id = this.roomId!!
    return backgroundSuspend { roomQueries.selectById(id).executeAsOne() }
}