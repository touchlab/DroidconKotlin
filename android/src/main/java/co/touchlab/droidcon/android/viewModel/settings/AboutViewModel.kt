package co.touchlab.droidcon.android.viewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.android.service.ParseUrlViewService
import co.touchlab.droidcon.application.repository.AboutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AboutViewModel: ViewModel(), KoinComponent {

    val items = MutableStateFlow<List<AboutItemViewModel>>(emptyList())

    private val aboutRepository by inject<AboutRepository>()
    private val parseUrlViewService by inject<ParseUrlViewService>()

    init {
        viewModelScope.launch {
            items.value = aboutRepository.getAboutItems()
                .map {
                    val links = parseUrlViewService.parse(it.detail)
                    // TODO: How to get an image from icon String?
                    AboutItemViewModel(it.title, it.detail, links, it.icon.toInt())
                }
        }
    }
}