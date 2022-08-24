package co.touchlab.droidcon.dto

import co.touchlab.droidcon.composite.Url

data class WebLink(val range: IntRange, val link: String) {

    companion object {

        fun fromUrl(url: Url): WebLink =
            WebLink(IntRange(0, url.string.length - 1), url.string)
    }
}
