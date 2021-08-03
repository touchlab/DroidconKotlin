package co.touchlab.droidcon.android.util

import android.content.Context
import co.touchlab.droidcon.R
import co.touchlab.droidcon.application.service.NotificationSchedulingService

class NotificationLocalizedStringFactory(
    private val context: Context,
): NotificationSchedulingService.LocalizedStringFactory {

    override fun title(roomName: String?): String {
        val ending = roomName?.let { context.getString(R.string.notification_title_in_room, it) } ?: ""
        return context.getString(R.string.notification_title_base, ending)
    }

    override fun body(sessionTitle: String): String {
        return context.getString(R.string.notification_body, sessionTitle)
    }
}