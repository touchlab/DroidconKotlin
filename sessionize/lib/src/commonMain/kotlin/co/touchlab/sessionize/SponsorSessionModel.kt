package co.touchlab.sessionize

import co.touchlab.droidcon.db.Sponsor
import co.touchlab.sessionize.db.SessionizeDbHelper.sponsorQueries
import co.touchlab.sessionize.platform.backgroundSuspend
import co.touchlab.sessionize.platform.logException

class SponsorSessionModel(val sponsorId: String) : BaseQueryModelView<Sponsor, SponsorSessionInfo>(
        sponsorQueries.sponsorById(sponsorId),
        { q ->
            val sponsor = q.executeAsOne()
            collectSponsorInfo(sponsor)
        },
        ServiceRegistry.coroutinesDispatcher) {

    init {
        ServiceRegistry.clLogCallback("init SponsorSessionModel($sponsorId)")
    }

    interface SponsorSessionView : View<SponsorSessionInfo>

    private suspend fun sendAnalytics(sponsorId: String) {

        try {
            val sponsor = backgroundSuspend {
                sponsorQueries.sponsorById(sponsorId).executeAsOne()
            }

            val params = HashMap<String, Any>()
            params["sponsorId"] = sponsorId

//            ServiceRegistry.analyticsApi.logEvent("RSVP_EVENT", params)
        } catch (e: Exception) {
            logException(e)
        }
    }
}

internal fun collectSponsorInfo(sponsor: Sponsor): SponsorSessionInfo{
//    val speakers = userAccountQueries.selectBySession(sponsor.sponsorId).executeAsList()
//    val mySessions = sponsorQueries.mys().executeAsList()

    return SponsorSessionInfo(sponsor) //, speakers, session.conflict(mySessions))
}

data class SponsorSessionInfo(
        val sponsor: Sponsor
//        val speakers: List<UserAccount>
)
