package co.touchlab.sessionize

import co.touchlab.droidcon.db.Sponsor
import co.touchlab.sessionize.AppContext.sponsorQueries
import co.touchlab.sessionize.db.SponsorGroupDbItem

class SponsorModel : BaseQueryModelView<Sponsor, List<SponsorGroupDbItem>>(
        sponsorQueries.selectAll(),
        {
            val sponsors = it.executeAsList()
            val groupList = mutableMapOf<String, ArrayList<Sponsor>>()

            for(sponsor in sponsors) {
                if(groupList.containsKey(sponsor.groupName)) {
                    groupList[sponsor.groupName]!!.add(sponsor)
                } else {
                    groupList[sponsor.groupName] = arrayListOf(sponsor)
                }
            }

            val finalList = arrayListOf<SponsorGroupDbItem>()
            groupList.forEach { list -> finalList.add(SponsorGroupDbItem(list.key, list.value)) }
            finalList
        },
        AppContext.dispatcherLocal.lateValue) {

    interface SponsorView : View<List<SponsorGroupDbItem>>

    init {
        clLog("init SponsorModel()")
    }
}