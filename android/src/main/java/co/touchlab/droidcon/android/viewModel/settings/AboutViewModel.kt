package co.touchlab.droidcon.android.viewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.R
import co.touchlab.droidcon.application.repository.AboutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AboutViewModel: ViewModel(), KoinComponent {

    val items = MutableStateFlow<List<AboutItemViewModel>>(emptyList())

    private val aboutRepository by inject<AboutRepository>()

    init {
        viewModelScope.launch {
            items.value = aboutRepository.getAboutItems()
                .map {
                    val iconRes = when (it.icon) {
                        "about_droidcon" -> R.drawable.about_droidcon
                        "about_kotlin" -> R.drawable.about_kotlin
                        "about_touchlab" -> R.drawable.about_touchlab
                        else -> R.drawable.menu_info
                    }
                    AboutItemViewModel(it.title, it.detail, it.link, iconRes)
                }
        }
    }
}