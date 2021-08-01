package co.touchlab.droidcon.domain.service.impl

interface ResourceReader {
    fun readResource(name: String): String
}