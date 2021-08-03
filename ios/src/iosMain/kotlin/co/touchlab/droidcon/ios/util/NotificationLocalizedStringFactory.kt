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

    override fun title(roomName: String?): String {
        val ending = roomName?.let { NSString.stringWithFormat(bundle.localizedStringForKey("Notification.Title.InRoom", null, null), it.cstr) } ?: ""
        return NSString.stringWithFormat(bundle.localizedStringForKey("Notification.Title.Base", null, null), ending.cstr)
    }

    override fun body(sessionTitle: String): String {
        return NSString.stringWithFormat(bundle.localizedStringForKey("Notification.Body", null, null), sessionTitle.cstr)
    }
}