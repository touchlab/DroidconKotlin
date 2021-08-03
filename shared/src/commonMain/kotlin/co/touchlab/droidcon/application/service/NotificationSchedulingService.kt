package co.touchlab.droidcon.application.service

interface NotificationSchedulingService {
    companion object {
        // MARK: Delivery offsets (in minutes)
        const val REMINDER_DELIVERY_OFFSET: Long = -10
        const val REMINDER_DISMISS_OFFSET: Long = 20
    }

    suspend fun runScheduling()

    interface LocalizedStringFactory {

        fun title(roomName: String?): String
        fun body(sessionTitle: String): String
    }
}