package co.touchlab.droidcon.android.service.impl

import android.util.Patterns
import co.touchlab.droidcon.dto.WebLink
import co.touchlab.droidcon.service.ParseUrlViewService

class DefaultParseUrlViewService : ParseUrlViewService {

    private val urlRegex = Patterns.WEB_URL.toRegex()

    override fun parse(text: String): List<WebLink> = urlRegex.findAll(text).map { WebLink(it.range, it.value) }.toList()
}
