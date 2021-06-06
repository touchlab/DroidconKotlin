package co.touchlab.sessionize

import co.touchlab.sessionize.jsondata.SponsorGroup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SponsorViewModel {
    fun load(
            proc: (List<SponsorGroup>) -> Unit,
            error: (ex: Throwable) -> Unit
    ) {
        // TODO: Replace this with `viewModelScope`
        //  this should only be temporary.
        //  The goal, for now, is to simply to make
        //  project work. This will be corrected later on.
        GlobalScope.launch {
            SponsorsModel.loadSponsors(proc, error)
        }
    }
}