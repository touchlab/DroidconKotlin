package co.touchlab.droidcon.ios.service

import co.touchlab.droidcon.dto.WebLink
import co.touchlab.droidcon.service.ParseUrlViewService

class DefaultParseUrlViewService : ParseUrlViewService {

    private val urlRegex =
        "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)".toRegex()

    override fun parse(text: String): List<WebLink> {
        return urlRegex.findAll(text).map { WebLink(it.range, it.value) }.toList()
    }
}
