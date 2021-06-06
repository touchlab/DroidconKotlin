package co.touchlab.sessionize.db

import co.touchlab.droidcon.db.DroidconDb
import co.touchlab.droidcon.db.RoomQueries
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.SessionQueries
import co.touchlab.droidcon.db.SessionSpeakerQueries
import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.droidcon.db.SponsorSessionQueries
import co.touchlab.droidcon.db.UserAccountQueries
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.api.parseSessionsFromDays
import co.touchlab.sessionize.jsondata.SessionSpeaker
import co.touchlab.sessionize.jsondata.Speaker
import co.touchlab.sessionize.jsondata.SponsorSessionGroup
import co.touchlab.sessionize.platform.printThrowable
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.freeze
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object SessionizeDbHelper {

    private val driverRef = AtomicReference<SqlDriver?>(null)
    private val dbRef = AtomicReference<DroidconDb?>(null)

    fun initDatabase(sqlDriver: SqlDriver) {
        driverRef.value = sqlDriver.freeze()
        dbRef.value = DroidconDb(sqlDriver, Session.Adapter(
                startsAtAdapter = DateAdapter(), endsAtAdapter = DateAdapter()
        )).freeze()
    }

    internal fun dbClear() {
        dbRef.value = null
        driverRef.value?.close()
        driverRef.value = null
    }

    internal val instance: DroidconDb
        get() = dbRef.value!!

    fun getSessionsQuery(): Query<SessionWithRoom> = instance.sessionQueries.sessionWithRoom()

    fun updateFeedback(feedbackRating: Long?, feedbackComment: String?, id: String) = instance.sessionQueries.updateFeedBack(feedbackRating,feedbackComment,id)

    suspend fun sendFeedback(){
        val sessions = allFeedbackToSend()

        sessions.forEach {
            val rating = it.feedbackRating
            if(rating != null) {
                if(ServiceRegistry.sessionizeApi.sendFeedback(it.id, rating.toInt(), it.feedbackComment)){
                    instance.sessionQueries.updateFeedBackSent(it.id)
                }
            }
        }
    }

    internal suspend fun allFeedbackToSend() = withContext(ServiceRegistry.backgroundDispatcher) {
        instance.sessionQueries.sessionFeedbackToSend().executeAsList()
    }

    fun primeAll(speakerJson: String, scheduleJson: String, sponsorSessionJson: String) {
        instance.sessionQueries.transaction {
            try {
                primeSpeakers(speakerJson)
                primeSessions(scheduleJson)
                primeSponsorSessions(sponsorSessionJson)
            } catch (e: Exception) {
                printThrowable(e)
                throw e
            }
        }
    }

    private fun primeSpeakers(speakerJson: String) {
        val speakers = Json.decodeFromString<List<Speaker>>(speakerJson)

        for (speaker in speakers) {
            var twitter: String? = null
            var linkedIn: String? = null
            var blog: String? = null
            var other: String? = null
            var companyWebsite: String? = null


            for (link in speaker.links) {

                if (link.linkType == "Twitter") {
                    twitter = link.url
                } else if (link.linkType == "LinkedIn") {
                    linkedIn = link.url
                } else if (link.linkType == "Blog") {
                    blog = link.url
                } else if (link.linkType == "Other") {
                    other = link.url
                } else if (link.linkType == "Company_Website") {
                    companyWebsite = link.url
                }

            }

            instance.userAccountQueries.insertUserAccount(
                    speaker.id,
                    speaker.fullName,
                    speaker.bio,
                    speaker.tagLine,
                    speaker.profilePicture,
                    twitter,
                    linkedIn,
                    if (!companyWebsite.isNullOrEmpty()) {
                        companyWebsite
                    } else if (!blog.isNullOrEmpty()) {
                        blog
                    } else {
                        other
                    }
            )
        }
    }

    private fun primeSessions(scheduleJson: String) {
        val sessions = parseSessionsFromDays(scheduleJson)

        instance.sessionSpeakerQueries.deleteAll()
        val allSessions = instance.sessionQueries.allSessions().executeAsList()

        val newIdSet = HashSet<String>()

        for (session in sessions) {
            instance.roomQueries.insertRoot(session.roomId!!.toLong(), session.room)

            val sessionId = session.id
            newIdSet.add(sessionId)

            val dbSession = instance.sessionQueries.sessionById(sessionId).executeAsOneOrNull()


            val startsAt = session.startsAt!!
            val endsAt = session.endsAt!!

            val sessionDateAdapter = DateAdapter()
            if (dbSession == null) {
                instance.sessionQueries.insert(
                        sessionId,
                        session.title,
                        session.descriptionText ?: "",
                        sessionDateAdapter.decode(startsAt),
                        sessionDateAdapter.decode(endsAt),
                        if (session.isServiceSession) {
                            1
                        } else {
                            0
                        }, session.roomId!!.toLong()
                )
            } else {
                instance.sessionQueries.update(
                        title = session.title,
                        description = session.descriptionText ?: "",
                        startsAt = sessionDateAdapter.decode(startsAt),
                        endsAt = sessionDateAdapter.decode(endsAt),
                        serviceSession = if (session.isServiceSession) {
                            1
                        } else {
                            0
                        },
                        roomId = session.roomId!!.toLong(),
                        rsvp = dbSession.rsvp,
                        feedbackRating =  dbSession.feedbackRating,
                        feedbackComment = dbSession.feedbackComment,
                        id = sessionId
                )
            }

            val speakers = session.speakers

            insertSessionSpeakers(speakers, sessionId)
        }

        allSessions.forEach {
            if (!newIdSet.contains(it.id)) {
                instance.sessionQueries.deleteById(it.id)
            }
        }
    }

    private fun insertSessionSpeakers(speakers: List<SessionSpeaker>, sessionId: String) {
        var displayOrder = 0L

        for (sessionSpeaker in speakers) {
            instance.sessionSpeakerQueries.insertUpdate(
                    sessionId,
                    sessionSpeaker.id,
                    displayOrder++)
        }
    }

    private fun primeSponsorSessions(sponsorSessionsJson: String) {
        val sponsorSessionGroups = Json.decodeFromString<List<SponsorSessionGroup>>(sponsorSessionsJson)

        for (sessionGroup in sponsorSessionGroups) {
            for (session in sessionGroup.sessions) {
                instance.sponsorSessionQueries.insertUpdate(session.id, session.descriptionText)
                insertSessionSpeakers(session.speakers, session.id)
            }
        }
    }

    val sessionQueries: SessionQueries
        get() = instance.sessionQueries

    val userAccountQueries: UserAccountQueries
        get() = instance.userAccountQueries

    val roomQueries: RoomQueries
        get() = instance.roomQueries

    val sponsorSessionQueries: SponsorSessionQueries
        get() = instance.sponsorSessionQueries

    val sessionSpeakerQueries: SessionSpeakerQueries
        get() = instance.sessionSpeakerQueries
}
