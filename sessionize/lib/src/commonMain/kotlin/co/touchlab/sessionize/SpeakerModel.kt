package co.touchlab.sessionize

import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.multiplatform.architecture.livedata.MutableLiveData
import co.touchlab.multiplatform.architecture.livedata.map
import co.touchlab.sessionize.AppContext.userAccountQueries
import co.touchlab.sessionize.db.QueryLiveData
import co.touchlab.sessionize.db.sessions
import co.touchlab.stately.freeze
import com.squareup.sqldelight.Query
import kotlinx.coroutines.launch

class SpeakerModel(speakerId:String) : BaseModel(AppContext.dispatcherLocal.lateValue){

    val speakerLiveData:SpeakerLiveData

    init {
        clLog("init SpeakerModel($speakerId)")
        speakerLiveData = SpeakerLiveData(userAccountQueries.selectById(speakerId).freeze())
    }

    fun shutDown(){
        speakerLiveData.removeListener()
    }

    /**
     * A little hacky, but it'll work
     */
    fun processUser(it: UserAccount, block:(SpeakerUiData)->Unit) = launch{
        val infoSections = ArrayList<SpeakerInfo>()

        if(!it.tagLine.isNullOrBlank())
            infoSections.add(SpeakerInfo(InfoType.Company, it.tagLine!!))
        if(!it.website.isNullOrBlank())
            infoSections.add(SpeakerInfo(InfoType.Website, it.website!!))
        if(!it.twitter.isNullOrBlank())
            infoSections.add(SpeakerInfo(InfoType.Twitter, it.twitter!!))
        if(!it.linkedIn.isNullOrBlank())
            infoSections.add(SpeakerInfo(InfoType.Linkedin, it.linkedIn!!))
        if(!it.bio.isNullOrBlank())
            infoSections.add(SpeakerInfo(InfoType.Profile, it.bio!!))

        val suid = SpeakerUiData(it, it.fullName, it.tagLine, it.profilePicture, infoSections, it.sessions())
        block(suid)
    }

    class SpeakerLiveData(q: Query<UserAccount>) : QueryLiveData<UserAccount, UserAccount>(q) {
        override fun extractData(q: Query<UserAccount>) = q.executeAsOne()
    }
}

data class SpeakerUiData(val user: UserAccount,
                         val fullName: String,
                         val company: String?,
                         val profilePicture: String?,
                         val infoRows: List<SpeakerInfo>,
                         val sessions: List<Session>)

data class SpeakerInfo(val type: InfoType, val info:String)

enum class InfoType(val icon:String) {
    Company("icon_company"), Website("icon_website"), Twitter("icon_twitter"),
    Linkedin("icon_linkedin"), Profile("icon_profile")
}