package co.touchlab.sessionize

import co.touchlab.sessionize.db.SponsorGroupDbItem

class SponsorViewModel {
    val sponsorModel = SponsorModel()

    fun registerForChanges(proc:(data: List<SponsorGroupDbItem>)->Unit){
        sponsorModel.register(object : SponsorModel.SponsorView {
            override suspend fun update(data: List<SponsorGroupDbItem>) {
                proc(data)
            }
        })
    }

    fun unregister(){
        sponsorModel.shutDown()
    }
}