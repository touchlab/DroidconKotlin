package co.touchlab.droidcon.application.service

import co.touchlab.droidcon.domain.entity.Session

sealed interface Notification {
    sealed interface DeepLink : Notification

    sealed interface Local : Notification {
        data class Reminder(
            val sessionId: Session.Id,
        ) : Local, DeepLink

        data class Feedback(
            val sessionId: Session.Id,
        ) : Local, DeepLink
    }

    sealed interface Remote : Notification {
        data object RefreshData : Remote
    }

    object Keys {
        const val notificationType = "notification_type"
        const val sessionId = "session_id"
    }

    object Values {
        const val reminderType = "reminder"
        const val feedbackType = "feedback"
        const val refreshDataType = "refresh_data"
    }
}
