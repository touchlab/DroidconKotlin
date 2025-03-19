package co.touchlab.droidcon.domain.service

import co.touchlab.droidcon.domain.entity.Conference

interface SyncService {

    suspend fun runSynchronization(conference: Conference)

    suspend fun forceSynchronize(conference:Conference): Boolean
}
