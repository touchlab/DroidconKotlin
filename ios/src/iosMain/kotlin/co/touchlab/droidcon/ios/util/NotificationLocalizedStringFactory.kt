package co.touchlab.droidcon.ios.util

import co.touchlab.droidcon.application.service.NotificationSchedulingService
import kotlinx.cinterop.cstr
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.stringWithFormat
import platform.Foundation.stringWithString

class NotificationLocalizedStringFactory(
    private val bundle: NSBundle,
): NotificationSchedulingService.LocalizedStringFactory {

    override fun reminderTitle(roomName: String?): String {
        val ending = roomName?.let { NSString.stringWithFormat(bundle.localizedStringForKey("Notification.Reminder.Title.InRoom", null, null), it.cstr) } ?: ""
        return NSString.stringWithFormat(bundle.localizedStringForKey("Notification.Reminder.Title.Base", null, null), ending.cstr)
    }

    override fun reminderBody(sessionTitle: String): String {
        return NSString.stringWithFormat(bundle.localizedStringForKey("Notification.Reminder.Body", null, null), sessionTitle.cstr)
    }

    override fun feedbackTitle(): String {
        return NSString.stringWithFormat(bundle.localizedStringForKey("Notification.Feedback.Title", null, null))
    }

    override fun feedbackBody(): String {
        return NSString.stringWithFormat(bundle.localizedStringForKey("Notification.Feedback.Body", null, null))
    }
}