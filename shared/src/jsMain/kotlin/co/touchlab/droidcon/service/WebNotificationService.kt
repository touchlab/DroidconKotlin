package co.touchlab.droidcon.service

import co.touchlab.droidcon.application.service.Notification
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.entity.Session
import kotlinx.datetime.Instant
import org.w3c.notifications.DENIED
import org.w3c.notifications.GRANTED
import org.w3c.notifications.Notification as W3cNotification
import org.w3c.notifications.NotificationOptions
import org.w3c.notifications.NotificationPermission

class WebNotificationService : NotificationService {
    private val notifications = mutableListOf<W3cNotification>()
    override suspend fun initialize(): Boolean = checkPermissions()

    // TODO: Does not schedule notification just shows it. Also we should confirm we want to actually add this for web
    override suspend fun schedule(notification: Notification.Local, title: String, body: String, delivery: Instant, dismiss: Instant?) {
        if (checkPermissions()) {
            val notif = W3cNotification(
                title,
                NotificationOptions(
                    body = body,
                    tag = (notification as? Notification.Local.Reminder)?.sessionId?.value
                        ?: (notification as? Notification.Local.Feedback)?.sessionId?.value,
                ),
            )
            notif.onclick = {

            }
            notifications.add(notif)
        }
    }

    override suspend fun cancel(sessionIds: List<Session.Id>) {
        notifications.removeAll { notifs -> sessionIds.any { it.value == notifs.tag }  }
    }

    override fun setHandler(notificationHandler: DeepLinkNotificationHandler) { }

    private fun checkPermissions(): Boolean {
        when (W3cNotification.permission) {
            NotificationPermission.GRANTED -> return true
            NotificationPermission.DENIED -> return false
            else -> {
                W3cNotification.requestPermission { }
                return false
            }
        }
    }
}
