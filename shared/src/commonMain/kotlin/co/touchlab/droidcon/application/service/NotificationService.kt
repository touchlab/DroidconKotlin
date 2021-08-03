package co.touchlab.droidcon.application.service

import co.touchlab.droidcon.domain.entity.Session
import kotlinx.datetime.Instant

interface NotificationService {
    suspend fun initialize(): Boolean

    suspend fun schedule(sessionId: Session.Id, title: String, body: String, delivery: Instant)

    suspend fun cancel(sessionIds: List<Session.Id>)
}