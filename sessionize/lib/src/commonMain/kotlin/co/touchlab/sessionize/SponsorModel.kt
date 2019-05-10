package co.touchlab.sessionize

import co.touchlab.droidcon.db.Sponsor
import co.touchlab.sessionize.db.SessionizeDbHelper.sponsorQueries
import co.touchlab.sessionize.db.SponsorGroupDbItem

class SponsorModel : BaseQueryModelView<Sponsor, List<SponsorGroupDbItem>>(
        sponsorQueries.selectAll(),
        {
            it.executeAsList().groupBy {
                sponsor -> sponsor.groupName
            }.map { item -> SponsorGroupDbItem(item.key, item.value) }
        },
        ServiceRegistry.coroutinesDispatcher) {

    interface SponsorView : View<List<SponsorGroupDbItem>>

    init {
        ServiceRegistry.clLogCallback("init SponsorModel()")
    }
}