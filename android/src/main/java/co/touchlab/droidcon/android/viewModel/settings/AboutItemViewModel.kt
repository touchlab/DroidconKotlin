package co.touchlab.droidcon.android.viewModel.settings

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.android.util.NamedDrawable
import co.touchlab.droidcon.composite.Url

class AboutItemViewModel(
    val title: String,
    val detail: String,
    val webLink: Url?,
    val image: NamedDrawable,
): ViewModel()