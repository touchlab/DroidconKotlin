package co.touchlab.droidcon.domain.service

interface SyncService {

    suspend fun runSynchronization()

    suspend fun forceSynchronize(): Boolean
}
