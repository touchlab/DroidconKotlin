package co.touchlab.sessionize.db

import co.touchlab.droidcon.db.Room
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.droidcon.db.UserAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun SessionWithRoom.isBlock(): Boolean = this.serviceSession != 0L
fun SessionWithRoom.isRsvp(): Boolean = this.rsvp != 0L

/**
 * Provide for "ORM-like" associated query
 */
internal suspend fun UserAccount.sessions(dbHelper: SessionizeDbHelper): List<Session> = withContext(Dispatchers.Default) {
    dbHelper.sessionQueries.userSessions(id).executeAsList()
}

internal suspend fun Session.room(dbHelper: SessionizeDbHelper): Room = withContext(Dispatchers.Default) {
    dbHelper.roomQueries.selectById(roomId!!).executeAsOne()
}