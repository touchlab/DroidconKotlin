package co.touchlab.droidcon.android.viewModel.settings

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.android.dto.WebLink

class AboutItemViewModel(
    val title: String,
    val detail: String,
    val webLinks: List<WebLink>,
    @DrawableRes
    val imageRes: Int?,
): ViewModel()