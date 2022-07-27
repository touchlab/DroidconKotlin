package co.touchlab.droidcon.ios.viewmodel.settings

import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class AboutItemViewModel(
    val title: String,
    val detail: String,
    val webLinks: List<WebLink>,
    val icon: String,
): BaseViewModel()
