package co.touchlab.sessionize.sponsors

//import co.touchlab.sessionize.SponsorsModel
import co.touchlab.sessionize.jsondata.SponsorGroup

class SponsorViewModel {
    fun load(
            proc: (List<SponsorGroup>) -> Unit,
            error: (ex: Throwable) -> Unit
    ) {
//        SponsorsModel.loadSponsors(proc, error)
    }
}