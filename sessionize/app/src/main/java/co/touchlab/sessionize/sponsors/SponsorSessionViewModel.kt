package co.touchlab.sessionize.sponsors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.touchlab.sessionize.SponsorSessionModel

class SponsorSessionViewModel(sponsorId:String): ViewModel(){
    val sponsorSessionModel = SponsorSessionModel(sponsorId)
}

class SponsorSessionViewModelFactory(private val sponsorId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SponsorSessionViewModel(sponsorId) as T
    }
}
