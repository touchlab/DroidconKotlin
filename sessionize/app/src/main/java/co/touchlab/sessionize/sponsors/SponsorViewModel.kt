package co.touchlab.sessionize.sponsors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.touchlab.sessionize.SponsorModel
import co.touchlab.sessionize.db.SponsorGroupDbItem

class SponsorViewModel : ViewModel(){
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

    class SponsorViewModelFactory : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SponsorViewModel() as T
        }
    }
}