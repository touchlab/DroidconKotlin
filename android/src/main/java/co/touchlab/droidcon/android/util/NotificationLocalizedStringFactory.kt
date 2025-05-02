package co.touchlab.droidcon.android.util

import android.content.Context
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import com.droidcon.app.R

class NotificationLocalizedStringFactory(private val context: Context) : NotificationSchedulingService.LocalizedStringFactory {

    override fun reminderTitle(roomName: String?): String {
        val ending = roomName?.let { context.getString(R.string.notification_reminder_title_in_room, it) } ?: ""
        return context.getString(R.string.notification_reminder_title_base, ending)
    }

    override fun reminderBody(sessionTitle: String): String = context.getString(R.string.notification_reminder_body, sessionTitle)

    override fun feedbackTitle(): String = context.getString(R.string.notification_feedback_title)

    override fun feedbackBody(): String = context.getString(R.string.notification_feedback_body)
}
