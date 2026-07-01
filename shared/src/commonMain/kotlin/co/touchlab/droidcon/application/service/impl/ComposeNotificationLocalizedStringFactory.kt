package co.touchlab.droidcon.application.service.impl

import co.touchlab.droidcon.application.service.NotificationSchedulingService
import droidcon.shared.generated.resources.Res
import droidcon.shared.generated.resources.notification_feedback_body
import droidcon.shared.generated.resources.notification_feedback_title
import droidcon.shared.generated.resources.notification_reminder_body
import droidcon.shared.generated.resources.notification_reminder_title_base
import droidcon.shared.generated.resources.notification_reminder_title_in_room
import org.jetbrains.compose.resources.getString

class ComposeNotificationLocalizedStringFactory : NotificationSchedulingService.LocalizedStringFactory {

    override suspend fun reminderTitle(roomName: String?): String {
        val ending = roomName?.let { getString(Res.string.notification_reminder_title_in_room, it) } ?: ""
        return getString(Res.string.notification_reminder_title_base, ending)
    }

    override suspend fun reminderBody(sessionTitle: String): String = getString(Res.string.notification_reminder_body, sessionTitle)

    override suspend fun feedbackTitle(): String = getString(Res.string.notification_feedback_title)

    override suspend fun feedbackBody(): String = getString(Res.string.notification_feedback_body)
}
