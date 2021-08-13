package co.touchlab.droidcon.domain.service

interface UserIdProvider {
    suspend fun getId(): String
}