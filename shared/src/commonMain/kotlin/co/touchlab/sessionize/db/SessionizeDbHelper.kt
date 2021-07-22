package co.touchlab.sessionize.db

import co.touchlab.droidcon.db.DroidconDb
import co.touchlab.droidcon.db.RoomQueries
import co.touchlab.droidcon.db.SessionQueries
import co.touchlab.droidcon.db.SessionSpeakerQueries
import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.droidcon.db.SponsorSessionQueries
import co.touchlab.droidcon.db.UserAccountQueries
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.api.parseSessionsFromDays
import co.touchlab.sessionize.backgroundDispatcher
import co.touchlab.sessionize.jsondata.SessionSpeaker
import co.touchlab.sessionize.jsondata.Speaker
import co.touchlab.sessionize.jsondata.SponsorSessionGroup
import co.touchlab.sessionize.timeZone
import com.squareup.sqldelight.Query
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import kotlin.native.concurrent.ThreadLocal

class SessionizeDbHelper(private val db:DroidconDb, private val sessionizeApi:SessionizeApi):KoinComponent {

    fun getSessionsQuery(): Query<SessionWithRoom> = db.sessionQueries.sessionWithRoom()

    fun updateFeedback(feedbackRating: Long?, feedbackComment: String?, id: String) = db.sessionQueries.updateFeedBack(feedbackRating,feedbackComment,id)

    suspend fun sendFeedback(){
        val sessions = allFeedbackToSend()

        sessions.forEach {
            val rating = it.feedbackRating
            if(sessionizeApi.sendFeedback(it.id, rating.toInt(), it.feedbackComment)){
                db.sessionQueries.updateFeedBackSent(it.id)
            }
        }
    }

    internal suspend fun allFeedbackToSend() = withContext(backgroundDispatcher) {
        db.sessionQueries.sessionFeedbackToSend().executeAsList()
    }

    fun primeAll(speakerJson: String, scheduleJson: String, sponsorSessionJson: String) {
        db.sessionQueries.transaction {
            try {
                primeSpeakers(speakerJson)
                primeSessions(scheduleJson)
                primeSponsorSessions(sponsorSessionJson)
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    private fun primeSpeakers(speakerJson: String) {
        val speakers = json.decodeFromString(ListSerializer(Speaker.serializer()), speakerJson)

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

            db.userAccountQueries.insertUserAccount(
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

        db.sessionSpeakerQueries.deleteAll()
        val allSessions = db.sessionQueries.allSessions().executeAsList()

        val newIdSet = HashSet<String>()

        for (session in sessions) {
            db.roomQueries.insertRoot(session.roomId!!.toLong(), session.room)

            val sessionId = session.id
            newIdSet.add(sessionId)

            val dbSession = db.sessionQueries.sessionById(sessionId).executeAsOneOrNull()


            val startsAt = session.startsAt!!
            val endsAt = session.endsAt!!

            val sessionDateAdapter = DateAdapter(timeZone)
            if (dbSession == null) {
                db.sessionQueries.insert(
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
                db.sessionQueries.update(
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
                db.sessionQueries.deleteById(it.id)
            }
        }
    }

    private fun insertSessionSpeakers(speakers: List<SessionSpeaker>, sessionId: String) {
        var displayOrder = 0L

        for (sessionSpeaker in speakers) {
            db.sessionSpeakerQueries.insertUpdate(
                    sessionId,
                    sessionSpeaker.id,
                    displayOrder++)
        }
    }

    private fun primeSponsorSessions(sponsorSessionsJson: String) {
        val sponsorSessionGroups = json.decodeFromString(
                ListSerializer(SponsorSessionGroup.serializer()),
                sponsorSessionsJson
        )

        for (sessionGroup in sponsorSessionGroups) {
            for (session in sessionGroup.sessions) {
                db.sponsorSessionQueries.insertUpdate(session.id, session.descriptionText)
                insertSessionSpeakers(session.speakers, session.id)
            }
        }
    }

    val sessionQueries: SessionQueries
        get() = db.sessionQueries

    val userAccountQueries: UserAccountQueries
        get() = db.userAccountQueries

    val roomQueries: RoomQueries
        get() = db.roomQueries

    val sponsorSessionQueries: SponsorSessionQueries
        get() = db.sponsorSessionQueries

    val sessionSpeakerQueries: SessionSpeakerQueries
        get() = db.sessionSpeakerQueries
}

@ThreadLocal
private val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
}

