package co.touchlab.droidcon.web.util

import co.touchlab.droidcon.domain.service.impl.ResourceReader
import org.w3c.xhr.XMLHttpRequest

class WebResourceReader : ResourceReader {
    override suspend fun readResource(name: String): String {
        val candidatePaths = listOf(
            "shared/src/commonMain/resources/$name",
            "/shared/src/commonMain/resources/$name",
            name,
            "/$name",
            "resources/$name",
            "/resources/$name",
        )

        candidatePaths.forEach { path ->
            val request = XMLHttpRequest()
            request.open("GET", path, false)
            request.send()

            if (request.status.toInt() in 200..299 && request.responseText.isNotBlank()) {
                return request.responseText
            }
        }

        error("Unable to load web resource: $name")
    }
}
