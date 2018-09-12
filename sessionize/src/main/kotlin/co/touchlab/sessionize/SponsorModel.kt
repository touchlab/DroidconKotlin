package co.touchlab.sessionize

import co.touchlab.sessionize.jsondata.DefaultData
import co.touchlab.sessionize.jsondata.SponsorGroup
import co.touchlab.sessionize.platform.backgroundTask

object SponsorModel{
    fun loadSponsor(proc:(aboutInfo:List<SponsorGroup>)->Unit){
        clLog("loadSponsor SponsorModel()")
        backgroundTask({
            DefaultData.parseSponsors(AppContext.sponsorJson)
        }, proc)
    }
}