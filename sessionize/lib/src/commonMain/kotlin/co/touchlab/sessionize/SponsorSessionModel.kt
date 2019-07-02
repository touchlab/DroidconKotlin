package co.touchlab.sessionize

import co.touchlab.droidcon.db.SessionSpeaker
import co.touchlab.droidcon.db.Sponsor
import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.db.SessionizeDbHelper.sessionSpeakerQueries
import co.touchlab.sessionize.db.SessionizeDbHelper.sponsorQueries
import co.touchlab.sessionize.db.SessionizeDbHelper.userAccountQueries
import co.touchlab.sessionize.platform.backgroundSuspend
import co.touchlab.sessionize.platform.logException

class SponsorSessionModel(val sponsorId: String, val groupName: String) : BaseQueryModelView<Sponsor, SponsorSessionInfo>(sponsorQueries.sponsorById(sponsorId, groupName),
        { q ->
            collectSponsorInfo(sponsorId, groupName)
        },
        ServiceRegistry.coroutinesDispatcher) {

    init {
        ServiceRegistry.clLogCallback("init SponsorSessionModel($sponsorId)")
        ServiceRegistry.analyticsApi.logEvent("sponsor_detail", mapOf(Pair("sponsorId", sponsorId), Pair("groupName", groupName)))
    }

    interface SponsorSessionView : View<SponsorSessionInfo>

    private suspend fun sendAnalytics(sponsorId: String) {

        try {
            val sponsor = backgroundSuspend {
                sponsorQueries.sponsorById(sponsorId, groupName).executeAsOne()
            }

            val params = HashMap<String, Any>()
            params["sponsorId"] = sponsorId
            params["name"] = sponsor.name

            ServiceRegistry.analyticsApi.logEvent("SPONSOR_VIEWED", params)
        } catch (e: Exception) {
            logException(e)
        }
    }
}

fun collectSponsorInfo(sponsorId: String, groupName: String): SponsorSessionInfo {
    val sponsor = sponsorQueries.sponsorById(sponsorId, groupName).executeAsOne()
    val speakers = userAccountQueries.selectBySession(sponsorId).executeAsList()

    return SponsorSessionInfo(sponsor, speakers)
}

data class SponsorSessionInfo(
        val sponsor: Sponsor,
        val speakers: List<UserAccount>
)
