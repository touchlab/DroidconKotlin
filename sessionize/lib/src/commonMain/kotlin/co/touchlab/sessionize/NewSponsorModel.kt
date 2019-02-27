package co.touchlab.sessionize

import co.touchlab.droidcon.db.Sponsor
import co.touchlab.sessionize.AppContext.sponsorQueries
import co.touchlab.sessionize.jsondata.NewSponsorGroup

class NewSponsorModel : BaseQueryModelView<Sponsor, List<NewSponsorGroup>>(
        sponsorQueries.selectAll(),
        {
            val sponsors = it.executeAsList()
            val groupList = mutableMapOf<String, MutableList<Sponsor>>()

            for(sponsor in sponsors) {
                if(groupList.containsKey(sponsor.groupName)) {
                    groupList[sponsor.groupName]!!.add(sponsor)
                } else {
                    groupList[sponsor.groupName] = mutableListOf(sponsor)
                }
            }

            val finalList = arrayListOf<NewSponsorGroup>()
            groupList.forEach { list -> finalList.add(NewSponsorGroup(list.key, list.value)) }
            finalList
        },
        AppContext.dispatcherLocal.lateValue) {

    interface SponsorView : View<List<NewSponsorGroup>>

    init {
        clLog("init SponsorModel()")
    }
}