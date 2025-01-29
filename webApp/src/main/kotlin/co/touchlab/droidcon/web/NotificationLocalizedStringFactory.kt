package co.touchlab.droidcon.web

import co.touchlab.droidcon.application.service.NotificationSchedulingService

class NotificationLocalizedStringFactory : NotificationSchedulingService.LocalizedStringFactory {

    override fun reminderTitle(roomName: String?): String {
        val ending = roomName?.let {
            DroidconStrings.notification_reminder_title_in_room.createString(it)
        } ?: ""
        return  DroidconStrings.notification_reminder_title_base.createString(ending)
    }

    override fun reminderBody(sessionTitle: String): String = DroidconStrings.notification_reminder_body.createString(sessionTitle)

    override fun feedbackTitle(): String = DroidconStrings.notification_feedback_title.toString()

    override fun feedbackBody(): String = DroidconStrings.notification_feedback_body.toString()
}
