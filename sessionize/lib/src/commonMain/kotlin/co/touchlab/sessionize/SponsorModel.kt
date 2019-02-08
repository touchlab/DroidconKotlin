package co.touchlab.sessionize

import co.touchlab.sessionize.jsondata.SponsorGroup
import co.touchlab.sessionize.platform.backgroundSuspend
import co.touchlab.stately.concurrency.value
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object SponsorModel : BaseModel(AppContext.dispatcherLocal.value!!) {
    fun loadSponsor(proc: (aboutInfo: List<SponsorGroup>) -> Unit) = launch {
        clLog("loadSponsor SponsorModel()")
        proc(backgroundSuspend {
            JSON.nonstrict.parse(SponsorGroup.serializer().list, AppContext.sponsorJson)
        })
    }
}