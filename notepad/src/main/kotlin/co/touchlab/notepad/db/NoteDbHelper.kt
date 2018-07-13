package co.touchlab.notepad.db

import co.touchlab.droidcon.db.QueryWrapper
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.notepad.data.DefaultData
import co.touchlab.notepad.sqldelight.Note
import co.touchlab.notepad.sqldelight.NoteQueries
import com.squareup.sqldelight.multiplatform.create

import co.touchlab.notepad.utils.initContext
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.SqlDatabase

class NoteDbHelper {

    private val queryWrapper: QueryWrapper
    private val database:SqlDatabase
    init {
        val helperFactory = initContext()
        database = QueryWrapper.create("holla6", openHelperFactory = helperFactory)
        queryWrapper = QueryWrapper(database)
    }

    fun getSpeakers(): Query<UserAccount> = queryWrapper.userAccountQueries.selectAll()

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
        val parseAll = DefaultData.parseAll(scheduleJson)
        queryWrapper.sessionSpeakerQueries.deleteAll()
        for (session in parseAll) {
            queryWrapper.roomQueries.insertRoot(session.roomId.toLong(), session.room)

            queryWrapper.sessionQueries.insertUpdate(
                    session.id, session.title, session.description, session.startsAt, session.endsAt,
                    if(session.serviceSession){1}else{0}, session.roomId.toLong()
            )

            var displayOrder = 0L
            for (sessionSpeaker in session.speakers) {
                queryWrapper.sessionSpeakerQueries.insertUpdate(
                        session.id,
                        sessionSpeaker.id,
                        displayOrder++)
            }
        }
    }
}
