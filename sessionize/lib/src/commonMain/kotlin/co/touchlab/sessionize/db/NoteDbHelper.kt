package co.touchlab.sessionize.db

import co.touchlab.droidcon.db.QueryWrapper
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.sessionize.jsondata.DefaultData
import co.touchlab.sessionize.platform.initSqldelightDatabase
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.SqlDatabase

class NoteDbHelper {

    val queryWrapper: QueryWrapper
    private val database:SqlDatabase

    init {
        val dateAdapter = DateAdapter()
        database = initSqldelightDatabase()
        queryWrapper = QueryWrapper(database, Session.Adapter(dateAdapter, dateAdapter))
    }

    fun getSessionsQuery(): Query<SessionWithRoom> = queryWrapper.sessionQueries.sessionWithRoom()

    fun primeAll(speakerJson:String, scheduleJson:String){
        queryWrapper.sessionQueries.transaction {
            primeSpeakers(speakerJson)
            primeSessions(scheduleJson)
        }

        queryWrapper.userAccountQueries.selectAll().executeAsList().forEach {
            println(it)
        }
    }

    private fun primeSpeakers(speakerJson:String){
        val speakers = DefaultData.parseSpeakers(speakerJson)
        for (speaker in speakers) {
            var twitter:String? = null
            var linkedIn:String? = null
            var blog:String? = null
            var other:String? = null
            var companyWebsite:String? = null


            for (link in speaker.links) {

                if(link.linkType == "Twitter"){
                    twitter = link.url
                }else if(link.linkType == "LinkedIn"){
                    linkedIn = link.url
                }else if(link.linkType == "Blog"){
                    blog = link.url
                }else if(link.linkType == "Other"){
                    other = link.url
                }else if(link.linkType == "Company_Website"){
                    companyWebsite = link.url
                }

            }

            queryWrapper.userAccountQueries.insertUserAccount(
                    speaker.id,
                    speaker.fullName,
                    speaker.bio,
                    speaker.tagLine,
                    speaker.profilePicture,
                    twitter,
                    linkedIn,
                    if(!companyWebsite.isNullOrEmpty()){
                        companyWebsite
                    }else if(!blog.isNullOrEmpty()){
                        blog
                    }else{
                        other
                    }
            )
        }
    }

    private fun primeSessions(scheduleJson:String){
        val parseAll = DefaultData.parseSchedule(scheduleJson)
        queryWrapper.sessionSpeakerQueries.deleteAll()
        val allSessions = queryWrapper.sessionQueries.allSessions().executeAsList()

        val newIdSet = HashSet<String>()

        for (session in parseAll) {
            queryWrapper.roomQueries.insertRoot(session.roomId.toLong(), session.room)

            newIdSet.add(session.id)

            val dbSession = queryWrapper.sessionQueries.sessionById(session.id).executeAsOneOrNull()

            if(dbSession == null) {
                queryWrapper.sessionQueries.insert(
                        session.id,
                        session.title,
                        session.description,
                        queryWrapper.sessionAdapter.startsAtAdapter.decode(session.startsAt),
                        queryWrapper.sessionAdapter.endsAtAdapter.decode(session.endsAt),
                        if (session.serviceSession) {
                            1
                        } else {
                            0
                        }, session.roomId.toLong()
                )
            }else{
                queryWrapper.sessionQueries.update(
                        title = session.title,
                        description = session.description,
                        startsAt = queryWrapper.sessionAdapter.startsAtAdapter.decode(session.startsAt),
                        endsAt = queryWrapper.sessionAdapter.endsAtAdapter.decode(session.endsAt),
                        serviceSession = if (session.serviceSession) {
                            1
                        } else {
                            0
                        },
                        roomId = session.roomId.toLong(),
                        rsvp = dbSession.rsvp,
                        id = session.id
                )
            }

            var displayOrder = 0L
            for (sessionSpeaker in session.speakers) {
                queryWrapper.sessionSpeakerQueries.insertUpdate(
                        session.id,
                        sessionSpeaker.id,
                        displayOrder++)
            }
        }

        allSessions.forEach {
            if(!newIdSet.contains(it.id)){
                queryWrapper.sessionQueries.deleteById(it.id)
            }
        }
    }
}
