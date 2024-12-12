package co.touchlab.droidcon.domain.service.impl.json

import co.touchlab.droidcon.domain.service.impl.ResourceReader
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json

class JsonResourceReader(private val resourceReader: ResourceReader, private val json: Json) {
    internal fun <T> readAndDecodeResource(name: String, strategy: DeserializationStrategy<T>): T {
        val text = resourceReader.readResource(name)
        return json.decodeFromString(strategy, text)
    }
}
