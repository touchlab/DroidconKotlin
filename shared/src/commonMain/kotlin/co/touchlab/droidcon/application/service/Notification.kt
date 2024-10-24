package co.touchlab.droidcon.application.service

import co.touchlab.droidcon.domain.entity.Session

sealed interface Notification {
    sealed interface DeepLink : Notification

    sealed interface Local : Notification {
        data class Reminder(val sessionId: Session.Id) :
            Local,
            DeepLink

        data class Feedback(val sessionId: Session.Id) :
            Local,
            DeepLink
    }

    sealed interface Remote : Notification {
        data object RefreshData : Remote
    }

    object Keys {
        const val NOTIFICATION_TYPE = "notification_type"
        const val SESSION_ID = "session_id"
    }

    object Values {
        const val REMINDER_TYPE = "reminder"
        const val FEEDBACK_TYPE = "feedback"
        const val REFRESH_DATA_TYPE = "refresh_data"
    }
}
