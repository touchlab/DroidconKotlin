package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.Durations
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.LOCAL_NOTIFICATIONS_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.api.notificationFeedbackTag
import co.touchlab.sessionize.api.notificationReminderTag
import co.touchlab.sessionize.db.SessionizeDbHelper.sessionQueries
import com.russhwolf.settings.set
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object NotificationsModel {
    fun notificationsEnabled(): Boolean {
        return ServiceRegistry.appSettings.getBoolean(LOCAL_NOTIFICATIONS_ENABLED, true)
    }

    fun feedbackEnabled(): Boolean {
        return ServiceRegistry.appSettings.getBoolean(FEEDBACK_ENABLED, true)
    }

    fun reminderNotificationsEnabled(): Boolean {
        return ServiceRegistry.appSettings.getBoolean(LOCAL_NOTIFICATIONS_ENABLED, true) &&
                ServiceRegistry.appSettings.getBoolean(REMINDERS_ENABLED, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        ServiceRegistry.appSettings[LOCAL_NOTIFICATIONS_ENABLED] = enabled
    }

    fun setRemindersEnabled(enabled: Boolean) {
        ServiceRegistry.appSettings[REMINDERS_ENABLED] = enabled
    }

    fun setFeedbackEnabled(enabled: Boolean) {
        ServiceRegistry.appSettings[FEEDBACK_ENABLED] = enabled
    }

    fun createNotificationsForSessions() {
        if (notificationsEnabled() && (reminderNotificationsEnabled() || feedbackEnabled())) {
            backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
                createReminderNotificationsForSessions(mySessions)
                createFeedbackNotificationsForSessions(mySessions)
            }
        }
    }

    fun cancelNotificationsForSessions() {
        if (!notificationsEnabled() || !reminderNotificationsEnabled() || !feedbackEnabled()) {
            backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
                cancelReminderNotificationsForSessions(mySessions)
                cancelFeedbackNotificationsForSessions(mySessions)
            }
        }
    }

    fun createReminderNotificationsForSessions(sessions: List<MySessions>) {
        if (reminderNotificationsEnabled() && notificationsEnabled()) {
            sessions.forEach { session ->
                val notificationTime = session.startsAt.toLongMillis() - Durations.TEN_MINS_MILLIS
                if (notificationTime > currentTimeMillis()) {
                    ServiceRegistry.notificationsApi.createLocalNotification("Upcoming Event in " + session.roomName,
                            session.title + " is starting soon.",
                            notificationTime,
                            session.id.hashCode(),
                            notificationReminderTag)
                }
            }
        }
    }

    fun createFeedbackNotificationsForSessions(sessions: List<MySessions>) {
        if (feedbackEnabled() && notificationsEnabled()) {
            sessions.forEach { session ->
                if (session.feedbackRating == null) {
                    val feedbackNotificationTime = session.endsAt.toLongMillis() + Durations.TEN_MINS_MILLIS
                    ServiceRegistry.notificationsApi.createLocalNotification("Feedback Time!",
                            "Your Feedback is Requested",
                            feedbackNotificationTime,
                            //Not great. Possible to clash, although super unlikely
                            session.id.hashCode(),
                            notificationFeedbackTag)
                }
            }
        }
    }

    fun cancelReminderNotificationsForSessions(sessions: List<MySessions>) {
        if (!reminderNotificationsEnabled() || !notificationsEnabled()) {
            sessions.forEach { session ->
                ServiceRegistry.notificationsApi.cancelLocalNotification(session.id.hashCode(), notificationReminderTag)
            }
        }
    }

    fun cancelFeedbackNotificationsForSessions(sessions: List<MySessions>) {
        if (!feedbackEnabled() || !notificationsEnabled()) {
            sessions.forEach { session ->
                ServiceRegistry.notificationsApi.cancelLocalNotification(session.id.hashCode(), notificationFeedbackTag)
            }
        }
    }
}