package co.touchlab.sessionize

import co.touchlab.droidcon.db.MySessions
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.db.QueryLiveData
import co.touchlab.sessionize.db.roomAsync
import co.touchlab.sessionize.platform.*
import co.touchlab.stately.freeze
import com.squareup.sqldelight.Query
import kotlinx.coroutines.launch
import kotlin.math.max

class EventModel(val sessionId: String) : BaseModel(AppContext.dispatcherLocal.value!!) {

    val evenLiveData: EventLiveData

    init {
        clLog("init EventModel($sessionId)")
        val query = AppContext.dbHelper.queryWrapper.sessionQueries.sessionById(sessionId).freeze()
        evenLiveData = EventLiveData(query).freeze()
    }

    fun shutDown() {
        evenLiveData.removeListener()
    }

    private val analyticsDateFormat = DateFormatHelper("MM_dd_HH_mm")
    fun toggleRsvp(rsvp: Boolean) {


        /*launch(ApplicationDispatcher) {
            val methodName = if(rsvp){"sessionizeRsvpEvent"}else{"sessionizeUnrsvpEvent"}
            val callingUrl = "dataTest/$methodName/$sessionId/${AppContext.userUuid()}"

            try {
                client.post {
                    url {
                        protocol = URLProtocol.HTTPS
                        port = 443
                        host = "droidcon-server.herokuapp.com"
                        encodedPath = callingUrl
                    }
                }
            } catch (e: Exception) {
                logException(e)
            }
        }*/

        val sessionIdLocal = sessionId

        networkBackgroundTask {
            val methodName = if (rsvp) {
                "sessionizeRsvpEvent"
            } else {
                "sessionizeUnrsvpEvent"
            }
            val callingUrl = "https://droidcon-server.herokuapp.com/dataTest/$methodName/$sessionIdLocal/${AppContext.userUuid()}"
            println("CALLING: $callingUrl")
            simpleGet(callingUrl)
        }

        /*backgroundTask({
            AppContext.dbHelper.queryWrapper.sessionQueries.sessionById(sessionIdLocal).executeAsOne()
        }) {
            val params = HashMap<String, Any>()
            params.put("slot", analyticsDateFormat.format(it.startsAt))
            params.put("sessionId", sessionIdLocal)
            params.put("count", if (rsvp) {
                1
            } else {
                -1
            })
            AppContext.logEvent("RSVP_EVENT", params)
        }*/
  /*      if(cont == null) {
            launch {
                println("suspend trace 1")*/
     /*           val session = backgroundSupend {
                    println("suspend trace 2")
                    val executeAsOne = AppContext.dbHelper.queryWrapper.sessionQueries.sessionById(sessionId).executeAsOne()
                    println("Internal session $executeAsOne")
                    executeAsOne
                }
                println("Outer session $session")
                val params = HashMap<String, Any>()
                params.put("slot", analyticsDateFormat.format((session as Session).startsAt))
                params.put("sessionId", sessionId)
                params.put("count", if(rsvp){1}else{-1})
                AppContext.logEvent("RSVP_EVENT", params)

*/
    /*            backgroundSupendLight {
                    println("Running in background")
                }
                println("meh 2")

            }
        }else{
            val result = kotlin.Result.success("jjj")
            cont!!.resumeWith(result)
            cont = null
        }*/

        backgroundTask {
            AppContext.dbHelper.queryWrapper.sessionQueries.updateRsvp(if (rsvp) {
                1
            } else {
                0
            }, sessionIdLocal)
        }
    }

    class EventLiveData(q: Query<Session>) : QueryLiveData<Session, SessionInfo>(q, false),
            Query.Listener {
        override /*suspend*/ fun extractData(q: Query<Session>): SessionInfo {
            val sessionStuff = q.executeAsOne()
            val speakers = AppContext.dbHelper.queryWrapper.userAccountQueries.selectBySession(sessionStuff.id).executeAsList()
            val mySessions = AppContext.dbHelper.queryWrapper.sessionQueries.mySessions().executeAsList()

            return SessionInfo(sessionStuff, sessionStuff.formattedRoomTime(), speakers, conflict(sessionStuff, mySessions))
        }

        private fun conflict(session: Session, others: List<MySessions>): Boolean {
            if (session.rsvp == 0L)
                return false

            val now = currentTimeMillis()
            if (now <= session.endsAt.toLongMillis()) {
                for (other in others.filter {
                    now <= it.endsAt.toLongMillis() &&
                            session.id != it.id
                }) {
                    if (session.startsAt.toLongMillis() < other.endsAt.toLongMillis() &&
                            session.endsAt.toLongMillis() > other.startsAt.toLongMillis())
                        return true
                }
            }

            return false
        }
    }
}

data class SessionInfo(
        val session: Session,
        val formattedRoomTime: String,
        val speakers: List<UserAccount>,
        val conflict: Boolean
)

fun SessionInfo.isNow(): Boolean {
    val now = currentTimeMillis()
    return now < this.session.endsAt.toLongMillis() &&
            now > this.session.startsAt.toLongMillis()
}

fun SessionInfo.isPast(): Boolean {
    return currentTimeMillis() > this.session.endsAt.toLongMillis()
}

fun SessionInfo.isRsvped(): Boolean {
    return this.session.rsvp != 0L
}

/*suspend*/ fun Session.formattedRoomTime(): String {
    var formattedStart = SessionInfoStuff.roomNameTimeFormatter.format(this.startsAt)
    val formattedEnd = SessionInfoStuff.roomNameTimeFormatter.format(this.endsAt)

    val startMarker = formattedStart.substring(max(formattedStart.length - 3, 0))
    val endMarker = formattedEnd.substring(max(formattedEnd.length - 3, 0))
    if (startMarker == endMarker) {
        formattedStart = formattedStart.substring(0, max(formattedStart.length - 3, 0))
    }

    return "${this.roomAsync()./*await().*/name}, $formattedStart - $formattedEnd"
}

object SessionInfoStuff {
    private val TIME_FORMAT = "h:mm a"
    val roomNameTimeFormatter = DateFormatHelper(TIME_FORMAT)
}