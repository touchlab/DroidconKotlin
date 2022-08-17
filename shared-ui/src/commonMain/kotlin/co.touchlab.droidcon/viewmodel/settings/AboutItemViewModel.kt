package co.touchlab.droidcon.viewmodel.settings

import co.touchlab.droidcon.dto.WebLink
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class AboutItemViewModel(
    val title: String,
    val detail: String,
    val webLinks: List<WebLink>,
    val icon: String,
): BaseViewModel()
