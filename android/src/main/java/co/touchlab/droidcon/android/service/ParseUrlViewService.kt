package co.touchlab.droidcon.android.service

import co.touchlab.droidcon.android.dto.WebLink

interface ParseUrlViewService {

    fun parse(text: String): List<WebLink>
}