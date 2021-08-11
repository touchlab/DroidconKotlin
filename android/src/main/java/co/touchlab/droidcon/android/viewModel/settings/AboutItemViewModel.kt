package co.touchlab.droidcon.android.viewModel.settings

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.composite.Url

class AboutItemViewModel(
    val title: String,
    val detail: String,
    val webLink: Url?,
    @DrawableRes
    val imageRes: Int?,
): ViewModel()