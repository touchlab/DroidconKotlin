package co.touchlab.droidcon.application.service

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.service.NotificationHandler
import kotlinx.datetime.Instant

interface NotificationService {
    suspend fun initialize(): Boolean

    suspend fun schedule(type: NotificationType, sessionId: Session.Id, title: String, body: String, delivery: Instant, dismiss: Instant?)

    suspend fun cancel(sessionIds: List<Session.Id>)

    fun setHandler(notificationHandler: NotificationHandler)

    enum class NotificationType {
        Reminder, Feedback
    }
}
