package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository: Repository<Session.Id, Session> {

    fun observeAllAttending(): Flow<List<Session>>

    suspend fun allAttending(): List<Session>

    suspend fun setAttending(sessionId: Session.Id, attending: Boolean)
}
