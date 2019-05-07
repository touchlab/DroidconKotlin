package co.touchlab.sessionize.platform

import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.LOCAL_NOTIFICATIONS_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.db.SessionizeDbHelper
import com.russhwolf.settings.set
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object NotificationsModel {

    fun createNotificationsForSessions() {
        if (notificationsEnabled() && (reminderNotificationsEnabled() || feedbackEnabled())) {
            backgroundTask({ SessionizeDbHelper.sessionQueries.mySessions().executeAsList() }) { mySessions ->
                ServiceRegistry.notificationsApi.createReminderNotificationsForSessions(mySessions)
                ServiceRegistry.notificationsApi.createFeedbackNotificationsForSessions(mySessions)
            }
        }
    }

    fun cancelNotificationsForSessions() {
        if (!notificationsEnabled() || !reminderNotificationsEnabled() || !feedbackEnabled()) {
            backgroundTask({ SessionizeDbHelper.sessionQueries.mySessions().executeAsList() }) { mySessions ->
                ServiceRegistry.notificationsApi.cancelReminderNotificationsForSessions(mySessions)
                ServiceRegistry.notificationsApi.cancelFeedbackNotificationsForSessions(mySessions)
            }
        }
    }

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
}