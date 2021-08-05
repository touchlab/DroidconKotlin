package co.touchlab.droidcon.application.composite

import co.touchlab.droidcon.composite.Url

data class AboutItem(
    val icon: String,
    val title: String,
    val detail: String,
    val link: Url?,
)