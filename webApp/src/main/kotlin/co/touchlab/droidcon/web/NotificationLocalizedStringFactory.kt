package co.touchlab.droidcon.web

class NotificationLocalizedStringFactory { // : NotificationSchedulingService.LocalizedStringFactory {

    fun reminderTitle(roomName: String?): String {
        val ending = roomName?.let {
            DroidconStrings.notification_reminder_title_in_room.createString(it)
        } ?: ""
        return  DroidconStrings.notification_reminder_title_base.createString(ending)
    }

    fun reminderBody(sessionTitle: String): String = DroidconStrings.notification_reminder_body.createString(sessionTitle)

    fun feedbackTitle(): String = DroidconStrings.notification_feedback_title.toString()

    fun feedbackBody(): String = DroidconStrings.notification_feedback_body.toString()
}
