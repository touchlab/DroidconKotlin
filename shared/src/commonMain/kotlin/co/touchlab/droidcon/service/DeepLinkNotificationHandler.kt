package co.touchlab.droidcon.service

import co.touchlab.droidcon.application.service.Notification

interface DeepLinkNotificationHandler {
    fun handleDeepLinkNotification(notification: Notification.DeepLink)
}
