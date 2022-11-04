package co.touchlab.droidcon.application.service

interface NotificationSchedulingService {
    companion object {
        // MARK: Delivery offsets (in minutes)
        const val REMINDER_DELIVERY_START_OFFSET: Long = -10
        const val REMINDER_DISMISS_OFFSET: Long = 20
        const val FEEDBACK_DISMISS_END_OFFSET: Long = 10
    }

    suspend fun runScheduling()

    suspend fun rescheduleAll()

    interface LocalizedStringFactory {

        fun reminderTitle(roomName: String?): String
        fun reminderBody(sessionTitle: String): String

        fun feedbackTitle(): String
        fun feedbackBody(): String
    }
}
