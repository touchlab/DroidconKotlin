package co.touchlab.droidcon.viewmodel.settings
import androidx.lifecycle.ViewModel

import co.touchlab.droidcon.dto.WebLink

class AboutItemViewModel(val title: String, val detail: String, val webLinks: List<WebLink>, val icon: String) : ViewModel()
