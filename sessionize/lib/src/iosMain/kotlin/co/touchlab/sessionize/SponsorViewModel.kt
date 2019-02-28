package co.touchlab.sessionize

import co.touchlab.sessionize.jsondata.NewSponsorGroup

class SponsorViewModel {
    val sponsorModel = NewSponsorModel()

    fun registerForChanges(proc:(data: List<NewSponsorGroup>)->Unit){
        sponsorModel.register(object : NewSponsorModel.SponsorView {
            override suspend fun update(data: List<NewSponsorGroup>) {
                proc(data)
            }
        })
    }

    fun unregister(){
        sponsorModel.shutDown()
    }
}