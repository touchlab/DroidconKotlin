package co.touchlab.sessionize

import co.touchlab.droidcon.db.UserAccount
import co.touchlab.multiplatform.architecture.livedata.MutableLiveData
import co.touchlab.multiplatform.architecture.livedata.map
import co.touchlab.sessionize.db.QueryLiveData
import co.touchlab.sessionize.platform.goFreeze
import com.squareup.sqldelight.Query

class SpeakerModel(speakerId:String){

    private val speakerLiveData:SpeakerLiveData

    init {
        val query = goFreeze(AppContext.dbHelper.queryWrapper.userAccountQueries.selectById(speakerId))
        speakerLiveData = SpeakerLiveData(query)
    }

    fun uiLiveData():MutableLiveData<SpeakerUiData>{
        return speakerLiveData.map {
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

            SpeakerUiData(it, it.fullName, it.tagLine, it.profilePicture, infoSections)
        }
    }

    fun shutDown(){
        speakerLiveData.removeListener()
    }

    class SpeakerLiveData(q: Query<UserAccount>) : QueryLiveData<UserAccount, UserAccount>(q){
        override suspend fun extractData(q: Query<UserAccount>): UserAccount = q.executeAsOne()
    }


}

data class SpeakerUiData(val user: UserAccount,
                         val fullName: String,
                         val company: String?,
                         val profilePicture: String?,
                         val infoRows: List<SpeakerInfo>)

data class SpeakerInfo(val type: InfoType, val info:String)

enum class InfoType(val icon:String) {
    Company("icon_company"), Website("icon_website"), Twitter("icon_twitter"),
    Linkedin("icon_linkedin"), Profile("icon_profile")
}