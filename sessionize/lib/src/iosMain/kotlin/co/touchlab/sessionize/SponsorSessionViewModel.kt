package co.touchlab.sessionize

import co.touchlab.droidcon.db.Sponsor

class SponsorSessionViewModel(sponsorId: String, groupName: String) {
    val sponsorSessionModel = SponsorSessionModel(sponsorId, groupName)

    fun registerForChanges(proc: (sponsor: SponsorSessionInfo) -> Unit) {
        sponsorSessionModel.register(object : SponsorSessionModel.SponsorSessionView {
            override suspend fun update(data: SponsorSessionInfo) {
                proc(data)
            }
        })
    }

    fun unregister() {
        sponsorSessionModel.shutDown()
    }

    fun sponsorSessionInfo(sponsor: Sponsor): SponsorSessionInfo? {
        sponsor.sponsorId?.let {
            return collectSponsorInfo(it, sponsor.groupName)
        }
        return null
    }


}
