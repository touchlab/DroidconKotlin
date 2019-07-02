package co.touchlab.sessionize.db

import co.touchlab.droidcon.db.DroidconDb
import co.touchlab.droidcon.db.RoomQueries
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.SessionQueries
import co.touchlab.droidcon.db.SessionSpeakerQueries
import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.droidcon.db.SponsorQueries
import co.touchlab.droidcon.db.UserAccountQueries
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.api.parseSessionsFromDays
import co.touchlab.sessionize.jsondata.SessionSpeaker
import co.touchlab.sessionize.jsondata.Speaker
import co.touchlab.sessionize.jsondata.SponsorGroup
import co.touchlab.sessionize.jsondata.SponsorSession
import co.touchlab.sessionize.jsondata.SponsorSessionGroup
import co.touchlab.sessionize.platform.backgroundSuspend
import co.touchlab.sessionize.platform.logException
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.freeze
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

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
        val sessions = backgroundSuspend { instance.sessionQueries.sessionFeedbackToSend().executeAsList() }

        sessions.forEach {
            val rating = it.feedbackRating
            if(rating != null) {
                if(ServiceRegistry.sessionizeApi.sendFeedback(it.id, rating.toInt(), it.feedbackComment)){
                    instance.sessionQueries.updateFeedBackSent(it.id)
                }
            }
        }
    }

    fun primeAll(speakerJson: String, scheduleJson: String, sponsorJson: String, sponsorSessionJson: String) {
        instance.sessionQueries.transaction {
            try {
                primeSpeakers(speakerJson)
                primeSessions(scheduleJson)
                primeSponsors(sponsorJson, sponsorSessionJson)
            } catch (e: Exception) {
                logException(e)
                throw e
            }
        }
    }

    private fun primeSpeakers(speakerJson: String) {
        val speakers = Json.nonstrict.parse(Speaker.serializer().list, speakerJson)//DefaultData.parseSpeakers(speakerJson)

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

    private fun primeSponsors(sponsorJson: String, sponsorSessionsJson: String) {
        println("sponsorJson: $sponsorJson")
        val sponsorGroups = Json.nonstrict.parse(SponsorGroup.serializer().list, sponsorJson)
        val sponsorSessionGroups = Json.nonstrict.parse(SponsorSessionGroup.serializer().list, sponsorSessionsJson)

        instance.sponsorQueries.deleteAll()

        val sessionizeSponsors: MutableMap<String, SponsorSession> = mutableMapOf()
        sponsorSessionGroups.forEach {
            it.sessions.forEach {
                sessionizeSponsors.put(it.id, it)
            }
        }

        for(group in sponsorGroups) {
            for(sponsor in group.sponsors) {
                val sessionizeSession = if (!sponsor.sponsorId.isNullOrEmpty()) {
                    sessionizeSponsors.get(sponsor.sponsorId)
                } else {
                    null
                }

                instance.sponsorQueries.insert(
                        sponsor.name,
                        sponsor.url,
                        sponsor.icon,
                        group.groupName,
                        sponsor.sponsorId,
                        sessionizeSession?.descriptionText
                )

                sessionizeSession?.let {
                    insertSessionSpeakers(it.speakers, it.id)
                }
            }
        }
    }

    val sessionQueries: SessionQueries
        get() = instance.sessionQueries

    val userAccountQueries: UserAccountQueries
        get() = instance.userAccountQueries

    val roomQueries: RoomQueries
        get() = instance.roomQueries

    val sponsorQueries: SponsorQueries
        get() = instance.sponsorQueries

    val sessionSpeakerQueries: SessionSpeakerQueries
        get() = instance.sessionSpeakerQueries
}
