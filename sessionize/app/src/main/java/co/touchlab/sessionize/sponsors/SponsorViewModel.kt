package co.touchlab.sessionize.sponsors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.touchlab.sessionize.NewSponsorModel
import co.touchlab.sessionize.jsondata.NewSponsorGroup

class SponsorViewModel : ViewModel(){
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

    class SponsorViewModelFactory : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SponsorViewModel() as T
        }
    }
}