package co.touchlab.droidcon.web.util

import co.touchlab.droidcon.application.service.NotificationSchedulingService

class NotificationLocalizedStringFactory : NotificationSchedulingService.LocalizedStringFactory {

    override fun reminderTitle(roomName: String?): String {
        val ending = roomName?.let { " in $it" } ?: ""
        return "Upcoming event$ending"
    }

    override fun reminderBody(sessionTitle: String): String = "$sessionTitle is starting soon."

    override fun feedbackTitle(): String = "Feedback Time!"

    override fun feedbackBody(): String = "Your Feedback is Requested."
}
