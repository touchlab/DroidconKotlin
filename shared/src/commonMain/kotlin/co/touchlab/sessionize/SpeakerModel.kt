package co.touchlab.sessionize

import co.touchlab.droidcon.db.Session
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.db.SessionizeDbHelper.userAccountQueries
import co.touchlab.sessionize.db.sessions

class SpeakerModel(speakerId: String) : BaseQueryModelView<UserAccount, UserAccount>(
        userAccountQueries.selectById(speakerId),
        {
            it.executeAsOne()
        },
        ServiceRegistry.coroutinesDispatcher
) {
    init {
        ServiceRegistry.clLogCallback("init SpeakerModel($speakerId)")
    }

    interface SpeakerView : View<UserAccount>

    /**
     * A little hacky, but it'll work
     */
    suspend fun speakerUiData(it: UserAccount): SpeakerUiData {
        val infoSections = ArrayList<SpeakerInfo>()

        if (!it.tagLine.isNullOrBlank())
            infoSections.add(SpeakerInfo(InfoType.Company, it.tagLine!!))
        if (!it.website.isNullOrBlank())
            infoSections.add(SpeakerInfo(InfoType.Website, it.website!!))
        if (!it.twitter.isNullOrBlank())
            infoSections.add(SpeakerInfo(InfoType.Twitter, it.twitter!!))
        if (!it.linkedIn.isNullOrBlank())
            infoSections.add(SpeakerInfo(InfoType.Linkedin, it.linkedIn!!))
        if (!it.bio.isNullOrBlank())
            infoSections.add(SpeakerInfo(InfoType.Profile, it.bio!!))

        return SpeakerUiData(it, it.fullName, it.tagLine, it.profilePicture, infoSections, it.sessions())
    }
}

data class SpeakerUiData(val user: UserAccount,
                         val fullName: String,
                         val company: String?,
                         val profilePicture: String?,
                         val infoRows: List<SpeakerInfo>,
                         val sessions: List<Session>)

data class SpeakerInfo(val type: InfoType, val info: String)

enum class InfoType(val icon: String) {
    Company("icon_company"), Website("icon_website"), Twitter("icon_twitter"),
    Linkedin("icon_linkedin"), Profile("icon_profile")
}