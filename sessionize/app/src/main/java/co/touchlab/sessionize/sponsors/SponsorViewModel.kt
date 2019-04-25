package co.touchlab.sessionize.sponsors

import androidx.lifecycle.ViewModel
import co.touchlab.sessionize.SponsorModel
import co.touchlab.sessionize.db.SponsorGroupDbItem

class SponsorViewModel : ViewModel(){
    private val sponsorModel = SponsorModel()

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