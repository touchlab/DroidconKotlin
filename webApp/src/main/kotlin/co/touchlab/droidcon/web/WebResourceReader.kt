package co.touchlab.droidcon.web

import co.touchlab.droidcon.domain.service.impl.ResourceReader

class WebResourceReader : ResourceReader {
    override fun readResource(name: String): String = ""//Res.readBytes(name).toString
}
