package co.touchlab.sessionize

import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.AppContext.userAccountQueries
import co.touchlab.sessionize.db.QueryUpdater
import co.touchlab.sessionize.db.sessions
import kotlinx.coroutines.launch

class SpeakerModel(speakerId:String) : BaseModel(AppContext.dispatcherLocal.lateValue){
    internal var view : View? = null

    internal val speakerQueryUpdater = QueryUpdater(
            q = userAccountQueries.selectById(speakerId),
            extractData = {
                it.executeAsOne()
            },
            updateSource = {
                updateUi(it)
            }
    )
    init {
        clLog("init SpeakerModel($speakerId)")
    }

    fun register(view:View){
        this.view = view
        speakerQueryUpdater.refresh()
    }

    fun shutDown(){
        speakerQueryUpdater.destroy()
        view = null
    }

    interface View{
        fun update(speakerUiData: SpeakerUiData)
    }

    /**
     * A little hacky, but it'll work
     */
    private fun updateUi(it: UserAccount) = launch{
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

        view?.let {view ->
            view.update(SpeakerUiData(it, it.fullName, it.tagLine, it.profilePicture, infoSections, it.sessions()))
        }
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