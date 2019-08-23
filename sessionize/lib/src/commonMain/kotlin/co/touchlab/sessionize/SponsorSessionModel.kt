package co.touchlab.sessionize

import co.touchlab.droidcon.db.UserAccount
import co.touchlab.sessionize.db.SessionizeDbHelper.sponsorSessionQueries
import co.touchlab.sessionize.db.SessionizeDbHelper.userAccountQueries
import co.touchlab.sessionize.jsondata.Sponsor
import co.touchlab.sessionize.platform.backgroundSuspend
import co.touchlab.sessionize.platform.logException
import co.touchlab.sessionize.platform.printThrowable
import kotlinx.coroutines.launch
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object SponsorSessionModel : BaseModel(ServiceRegistry.coroutinesDispatcher) {

    //This is super ugly and I apologize, but the changes weren't finished in time and I need to release...
    var sponsor: Sponsor? by FrozenDelegate()

    /*init {
        sponsor?.let {
            ServiceRegistry.clLogCallback("init SponsorSessionModel(${it.sponsorId})")
            ServiceRegistry.analyticsApi.logEvent("sponsor_detail", mapOf(Pair("sponsorId", it.sponsorId
                    ?: ""), Pair("groupName", it.groupName)))
        }
    }*/

    fun loadSponsorDetail(proc: (SponsorSessionInfo) -> Unit, error: (Throwable) -> Unit) {
        val sponsorArg = sponsor
        if (sponsorArg != null && sponsorArg.sponsorId != null) {
            launch {
                try {
                    val dataPair = backgroundSuspend {
                        Pair(
                                sponsorSessionQueries.sponsorSessionById(sponsorArg.sponsorId).executeAsOne().description?:"",
                                userAccountQueries.selectBySession(sponsorArg.sponsorId).executeAsList()
                        )
                    }

                    proc(SponsorSessionInfo(sponsorArg, dataPair.first, dataPair.second))
                } catch (e: Exception) {
                    error(e)
                }
            }
        } else {
            error(IllegalStateException("Sponsor info not sound"))
        }
    }

    private suspend fun sendAnalytics() {

        try {
            sponsor?.let {
                val params = HashMap<String, Any>()
                params["sponsorId"] = it.sponsorId ?: ""
                params["name"] = it.name

                ServiceRegistry.analyticsApi.logEvent("SPONSOR_VIEWED", params)
            }

        } catch (e: Exception) {
            logException(e)
        }
    }

    interface View<VT> {
        suspend fun update(data: VT)
        fun error(t: Throwable) {
            printThrowable(t)
            ServiceRegistry.softExceptionCallback(t, t.message ?: "(Unknown View Error)")
        }
    }
}

data class SponsorSessionInfo(
        val sponsor: Sponsor,
        val sessionDetail: String,
        val speakers: List<UserAccount>
)
