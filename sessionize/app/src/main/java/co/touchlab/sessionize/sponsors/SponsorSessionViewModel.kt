package co.touchlab.sessionize.sponsors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.touchlab.sessionize.SponsorSessionModel

class SponsorSessionViewModel(
        sponsorId: String,
        groupName: String): ViewModel(){

    val sponsorSessionModel = SponsorSessionModel(sponsorId, groupName)
}

class SponsorSessionViewModelFactory(
        private val sponsorId: String,
        private val groupName: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SponsorSessionViewModel(sponsorId, groupName) as T
    }
}
