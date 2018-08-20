package co.touchlab.sessionize.db

import co.touchlab.droidcon.db.Room
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.AppContext
import co.touchlab.sessionize.platform.ApplicationDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

fun SessionWithRoom.isBlock():Boolean = this.serviceSession != 0L
fun SessionWithRoom.isRsvp():Boolean = this.rsvp != 0L

/**
 * Provide for "ORM-like" associated query
 */
internal fun UserAccount.sessionsAsync(): Deferred<List<Session>> {
    val id = this.id
    return async(ApplicationDispatcher) { AppContext.dbHelper.queryWrapper.sessionQueries.userSessions(id).executeAsList() }
}

internal fun Session.roomAsync(): Deferred<Room> {
    val id = this.roomId!!
    return async(ApplicationDispatcher) { AppContext.dbHelper.queryWrapper.roomQueries.selectById(id).executeAsOne() }
}