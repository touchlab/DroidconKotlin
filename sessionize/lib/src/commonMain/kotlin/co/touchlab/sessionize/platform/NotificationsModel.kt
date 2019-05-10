package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.MySessions
import co.touchlab.droidcon.db.Session
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

    const val feedbackId:Int = 1


    // Settings

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


    // Create Everything

    fun createNotificationsForSessions() {
        if (notificationsEnabled() && (reminderNotificationsEnabled() || feedbackEnabled())) {
            backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
                createReminderNotificationsForMySessions(mySessions)
                createFeedbackNotificationsForMySessions(mySessions)
            }
        }
    }

    fun cancelNotificationsForSessions() {
        if (!notificationsEnabled() || !reminderNotificationsEnabled() || !feedbackEnabled()) {
            backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
                cancelReminderNotificationsForMySessions(mySessions)
                cancelFeedbackNotificationsForMySessions(mySessions)
            }
        }
    }


    // Create Lists

    fun createReminderNotificationsForMySessions(sessions: List<MySessions>) {
        if (reminderNotificationsEnabled() && notificationsEnabled()) {
            sessions.forEach { session ->
                createReminderNotification(session.endsAt.toLongMillis(),session.id.hashCode(),session.title, session.roomName)
            }
        }
    }

    fun createFeedbackNotificationsForMySessions(sessions: List<MySessions>) {
        if (feedbackEnabled() && notificationsEnabled()) {
            sessions.forEach { session ->
                if (session.feedbackRating == null) {
                    createFeedbackNotification(session.endsAt.toLongMillis())
                }
            }
        }
    }

    fun createReminderNotificationsForSession(session: Session,roomName: String) {
        if (reminderNotificationsEnabled() && notificationsEnabled()) {
                createReminderNotification(session.endsAt.toLongMillis(),session.id.hashCode(),session.title, roomName)
        }
    }

    fun createFeedbackNotificationsForSession(session: Session) {
        if (feedbackEnabled() && notificationsEnabled()) {
            if (session.feedbackRating == null) {
                createFeedbackNotification(session.endsAt.toLongMillis())
            }
        }
    }

    // create Singular

    private fun createReminderNotification(startsAtTime: Long, sessionId: Int, title:String, roomName:String){
        val notificationTime = startsAtTime - Durations.TEN_MINS_MILLIS
        if (notificationTime > currentTimeMillis()) {
            ServiceRegistry.notificationsApi.createLocalNotification("Upcoming Event in $roomName",
                    "$title is starting soon.",
                    notificationTime,
                    sessionId,
                    notificationReminderTag)
        }
    }

    private fun createFeedbackNotification(endsAtTime: Long){
        val feedbackNotificationTime = endsAtTime + Durations.TEN_MINS_MILLIS
        ServiceRegistry.notificationsApi.createLocalNotification("Feedback Time!",
                "Your Feedback is Requested",
                feedbackNotificationTime,
                feedbackId,
                notificationFeedbackTag)
    }


    // Cancel List

    fun cancelReminderNotificationsForMySessions(sessions: List<MySessions>) {
        if (!reminderNotificationsEnabled() || !notificationsEnabled()) {
            sessions.forEach { session ->
                ServiceRegistry.notificationsApi.cancelLocalNotification(session.id.hashCode(), notificationReminderTag)
            }
        }
    }

    fun cancelFeedbackNotificationsForMySessions(sessions: List<MySessions>) {
        if (!feedbackEnabled() || !notificationsEnabled()) {
            sessions.forEach { session ->
                ServiceRegistry.notificationsApi.cancelLocalNotification(feedbackId, notificationFeedbackTag)
            }
        }
    }

    fun cancelReminderNotificationsForSession(session: Session) {
        if (!reminderNotificationsEnabled() || !notificationsEnabled()) {
            cancelReminderNotification(session.id.hashCode())
        }
    }

    fun cancelFeedbackNotificationsForSession() {
        if (!feedbackEnabled() || !notificationsEnabled()) {
            cancelFeedbackNotification()
        }
    }

    // Cancel Singular

    private fun cancelReminderNotification(sessionid: Int) {
        ServiceRegistry.notificationsApi.cancelLocalNotification(sessionid, notificationReminderTag)
    }

    private fun cancelFeedbackNotification() {
        ServiceRegistry.notificationsApi.cancelLocalNotification(feedbackId, notificationFeedbackTag)
    }
}