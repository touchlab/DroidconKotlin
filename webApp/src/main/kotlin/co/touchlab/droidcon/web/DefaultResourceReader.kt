package co.touchlab.droidcon.web

import co.touchlab.droidcon.domain.service.impl.ResourceReader

class DefaultResourceReader : ResourceReader {
    override fun readResource(name: String): String {
        val fs = js("require('fs')")
        return fs.readFileSync(name, "utf8") as? String ?: ""
    }
}
