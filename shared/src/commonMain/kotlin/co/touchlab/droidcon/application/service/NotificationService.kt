package co.touchlab.droidcon.application.service

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.service.DeepLinkNotificationHandler
import kotlinx.datetime.Instant

interface NotificationService {
    suspend fun initialize(): Boolean

    suspend fun schedule(
        notification: Notification.Local,
        title: String,
        body: String,
        delivery: Instant,
        dismiss: Instant?
    )

    suspend fun cancel(sessionIds: List<Session.Id>)

    fun setHandler(notificationHandler: DeepLinkNotificationHandler)
}
