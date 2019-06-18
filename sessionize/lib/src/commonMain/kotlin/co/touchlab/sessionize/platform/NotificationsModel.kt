package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.Durations
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.LOCAL_NOTIFICATIONS_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.db.SessionizeDbHelper.sessionQueries
import com.russhwolf.settings.set
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object NotificationsModel {

    // Settings

    fun notificationsEnabled(): Boolean {
        return ServiceRegistry.appSettings.getBoolean(LOCAL_NOTIFICATIONS_ENABLED, true)
    }

    fun feedbackEnabled(): Boolean {
        return ServiceRegistry.appSettings.getBoolean(FEEDBACK_ENABLED, true)
    }

    fun remindersEnabled(): Boolean {
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


    fun createNotifications(){
        if(notificationsEnabled()) {
            recreateReminderNotifications()
            recreateFeedbackNotifications()
        }
    }

    fun cancelNotifications(){
        cancelReminderNotifications(true)
        cancelFeedbackNotifications()
    }

    fun cancelFeedbackNotifications() = ServiceRegistry.notificationsApi.cancelFeedbackNotifications()
    fun cancelReminderNotifications(andDismissals: Boolean) = ServiceRegistry.notificationsApi.cancelReminderNotifications(andDismissals)

    fun recreateReminderNotifications() {
        cancelReminderNotifications(false)
        if (remindersEnabled()){
            backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
                if(mySessions.isNotEmpty()) {
                    ServiceRegistry.notificationsApi.scheduleReminderNotificationsForSessions(mySessions)
                }
            }
        }
    }

    fun recreateFeedbackNotifications() {
        cancelFeedbackNotifications()
        if (feedbackEnabled()){
            backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
                if(mySessions.isNotEmpty()) {
                    ServiceRegistry.notificationsApi.scheduleFeedbackNotificationsForSessions(mySessions)
                }
            }
        }
    }


    fun getReminderTimeFromSession(session:MySessions): Long = session.startsAt.toLongMillis() - Durations.TEN_MINS_MILLIS
    fun getReminderNotificationTitle(session: MySessions) = "Upcoming Event in ${session.roomName}"
    fun getReminderNotificationMessage(session: MySessions) = "${session.title} is starting soon."

    fun getFeedbackTimeFromSession(session:MySessions): Long = session.endsAt.toLongMillis() + Durations.TEN_MINS_MILLIS
    fun getFeedbackNotificationTitle() = "Feedback Time!"
    fun getFeedbackNotificationMessage() = "Your Feedback is Requested"
}