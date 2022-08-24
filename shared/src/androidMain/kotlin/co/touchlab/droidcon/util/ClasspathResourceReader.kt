package co.touchlab.droidcon.util

import co.touchlab.droidcon.domain.service.impl.ResourceReader
import java.io.InputStreamReader

class ClasspathResourceReader: ResourceReader {
    override fun readResource(name: String): String {
        // TODO: Catch Android-only exceptions and map them to common ones.
        return javaClass.classLoader?.getResourceAsStream(name).use { stream ->
            InputStreamReader(stream).use { reader ->
                reader.readText()
            }
        }
    }
}