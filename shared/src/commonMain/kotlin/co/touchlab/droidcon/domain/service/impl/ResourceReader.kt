package co.touchlab.droidcon.domain.service.impl

import droidcon.shared.generated.resources.Res

interface ResourceReader {
    suspend fun readResource(name: String): String
}

class ComposeResourceReader : ResourceReader {
    override suspend fun readResource(name: String): String {
        val bytes = Res.readBytes(name)
        val jsonString = bytes.decodeToString()
        return jsonString
    }
}
