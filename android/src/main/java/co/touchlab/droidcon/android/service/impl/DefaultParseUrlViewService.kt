package co.touchlab.droidcon.android.service.impl

import android.util.Patterns
import co.touchlab.droidcon.android.dto.WebLink
import co.touchlab.droidcon.android.service.ParseUrlViewService

class DefaultParseUrlViewService: ParseUrlViewService {

    private val urlRegex = Patterns.WEB_URL.toRegex()

    override suspend fun parse(text: String): List<WebLink> {
        return urlRegex.findAll(text).map { WebLink(it.range, it.value) }.toList()
    }
}