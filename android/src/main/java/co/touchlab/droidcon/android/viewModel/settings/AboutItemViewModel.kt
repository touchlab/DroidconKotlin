package co.touchlab.droidcon.android.viewModel.settings

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.android.util.NamedDrawable
import co.touchlab.droidcon.dto.WebLink

class AboutItemViewModel(
    val title: String,
    val detail: String,
    val webLinks: List<WebLink>,
    val image: NamedDrawable,
): ViewModel()
