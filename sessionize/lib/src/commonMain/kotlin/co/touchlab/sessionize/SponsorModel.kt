package co.touchlab.sessionize

import co.touchlab.sessionize.jsondata.DefaultData
import co.touchlab.sessionize.jsondata.SponsorGroup
import co.touchlab.sessionize.platform.backgroundSupend
import co.touchlab.sessionize.platform.backgroundTask
import co.touchlab.stately.annotation.ThreadLocal
import co.touchlab.stately.concurrency.value
import kotlinx.coroutines.launch

@ThreadLocal
object SponsorModel : BaseModel(AppContext.dispatcherLocal.value!!){
    fun loadSponsor(proc:(aboutInfo:List<SponsorGroup>)->Unit) = launch{
        clLog("loadSponsor SponsorModel()")
        proc(backgroundSupend { DefaultData.parseSponsors(AppContext.sponsorJson) })
    }
}