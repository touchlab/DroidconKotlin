package co.touchlab.droidcon.web.util

import co.touchlab.droidcon.application.service.NotificationSchedulingService

class NotificationLocalizedStringFactory : NotificationSchedulingService.LocalizedStringFactory {

    override fun reminderTitle(roomName: String?): String = ""
    override fun reminderBody(sessionTitle: String): String = ""

    override fun feedbackTitle(): String = ""

    override fun feedbackBody(): String = ""
}
