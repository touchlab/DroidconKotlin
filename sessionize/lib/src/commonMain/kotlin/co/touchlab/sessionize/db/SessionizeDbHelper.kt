package co.touchlab.sessionize.db

import co.touchlab.droidcon.db.QueryWrapper
import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.sessionize.jsondata.DefaultData
import co.touchlab.sessionize.platform.initSqldelightDatabase
import co.touchlab.sessionize.platform.logException
import co.touchlab.stately.freeze
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.concurrency.Lock
import co.touchlab.stately.concurrency.withLock
import com.squareup.sqldelight.Query
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class SessionizeDbHelper {

    val queryWrapper: QueryWrapper by FrozenLazy<SessionizeDbHelper, QueryWrapper> {
        QueryWrapper(initSqldelightDatabase(), Session.Adapter(DateAdapter(), DateAdapter()))
    }

    //This will be replaced when Stately 0.4.0 releases, along with sqldelight rc5(ish)
    class FrozenLazy<R, T>(private val producer:()->T) : ReadOnlyProperty<R, T>{
        override fun getValue(thisRef: R, property: KProperty<*>): T =
            lock.withLock {
                var value = valAtomic.value
                if(value == null) {
                    value = producer()
                    valAtomic.value = value.freeze()
                }
                value!!
            }
        private val valAtomic = AtomicReference<T?>(null)
        private val lock = Lock()
    }

    fun getSessionsQuery(): Query<SessionWithRoom> = queryWrapper.sessionQueries.sessionWithRoom()

    fun primeAll(speakerJson:String, scheduleJson:String){
        println("primeAll starting transaction")
        queryWrapper.sessionQueries.transaction {
            println("primeAll a")
            primeSpeakers(speakerJson)
            println("primeAll b")
            primeSessions(scheduleJson)
            println("primeAll c")
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

            println("primeSpeakers a")
            try {
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
            } catch (e: Exception) {
                logException(e)
            }
            println("primeSpeakers b")
        }
    }

    private fun primeSessions(scheduleJson:String){
        val parseAll = DefaultData.parseSchedule(scheduleJson)
        queryWrapper.sessionSpeakerQueries.deleteAll()
        val allSessions = queryWrapper.sessionQueries.allSessions().executeAsList()

        val newIdSet = HashSet<String>()

        println("primeSessions a")
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
        println("primeSessions b")

        allSessions.forEach {
            if(!newIdSet.contains(it.id)){
                queryWrapper.sessionQueries.deleteById(it.id)
            }
        }
    }
}
