package co.touchlab.droidcon.android.service

import co.touchlab.droidcon.android.dto.WebLink

interface ParseUrlViewService {

    suspend fun parse(text: String): List<WebLink>
}