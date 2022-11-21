package co.touchlab.droidcon.service

import co.touchlab.droidcon.application.service.NotificationService

interface NotificationHandler {
    fun notificationReceived(sessionId: String, notificationType: NotificationService.NotificationType)
}
