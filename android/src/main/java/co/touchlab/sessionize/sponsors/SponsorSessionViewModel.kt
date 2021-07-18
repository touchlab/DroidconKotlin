package co.touchlab.sessionize.sponsors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.touchlab.sessionize.SponsorSessionModel

class SponsorSessionViewModel(): ViewModel(){
    val sponsorSessionModel = SponsorSessionModel
}

class SponsorSessionViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SponsorSessionViewModel() as T
    }
}
