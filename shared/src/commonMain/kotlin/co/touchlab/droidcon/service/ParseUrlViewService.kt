package co.touchlab.droidcon.service

import co.touchlab.droidcon.dto.WebLink

interface ParseUrlViewService {

    fun parse(text: String): List<WebLink>
}
