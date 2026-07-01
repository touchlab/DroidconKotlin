package co.touchlab.droidcon.service

import co.touchlab.droidcon.application.service.Notification
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.entity.Session
import kotlinx.datetime.Instant

class JsNotificationService : NotificationService {
    override suspend fun initialize(): Boolean = false

    override suspend fun schedule(notification: Notification.Local, title: String, body: String, delivery: Instant, dismiss: Instant?) {
    }

    override suspend fun cancel(sessionIds: List<Session.Id>) {
    }

    override fun setHandler(notificationHandler: DeepLinkNotificationHandler) {
    }
}
