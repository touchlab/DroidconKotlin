package co.touchlab.sessionize.platform

import co.touchlab.sessionize.Durations
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.LOCAL_NOTIFICATIONS_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.api.notificationFeedbackId
import co.touchlab.sessionize.api.notificationFeedbackTag
import co.touchlab.sessionize.api.notificationReminderId
import co.touchlab.sessionize.api.notificationReminderTag
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
        recreateNotifications()
    }

    fun cancelNotificationsForSessions() {
        if (!notificationsEnabled() || !reminderNotificationsEnabled() || !feedbackEnabled()) {
            ServiceRegistry.notificationsApi.cancelLocalNotification(notificationReminderId, notificationReminderTag)
            cancelFeedbackNotification()
        }
    }

    // create Singular

    private fun createReminderNotification(startsAtTime: Long, title:String, message:String){
        val notificationTime = startsAtTime - Durations.TEN_MINS_MILLIS
        if (notificationTime > currentTimeMillis()) {
            ServiceRegistry.notificationsApi.createLocalNotification(title,
                                                                    message,
                                                                    notificationTime,
                                                                    notificationReminderId,
                                                                    notificationReminderTag)
        }
    }

    private fun createFeedbackNotification(endsAtTime: Long){
        val feedbackNotificationTime = endsAtTime + Durations.TEN_MINS_MILLIS
        ServiceRegistry.notificationsApi.createLocalNotification("Feedback Time!",
                "Your Feedback is Requested",
                feedbackNotificationTime,
                notificationFeedbackId,
                notificationFeedbackTag)
    }


    // Cancel List

    fun cancelFeedbackNotificationsForSession() {
        if (!feedbackEnabled() || !notificationsEnabled()) {
            cancelFeedbackNotification()
        }
    }

    // Cancel Singular

    private fun cancelFeedbackNotification() {
        ServiceRegistry.notificationsApi.cancelLocalNotification(notificationFeedbackId, notificationFeedbackTag)
    }

    fun recreateNotifications(){
        ServiceRegistry.notificationsApi.cancelLocalNotification(notificationReminderId, notificationReminderTag)
        cancelFeedbackNotification()

        if (notificationsEnabled()){
             backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
                 if(mySessions.isNotEmpty()) {
                     if (reminderNotificationsEnabled()) {

                         try {
                             val session = mySessions.first { it.startsAt.toLongMillis() - Durations.TEN_MINS_MILLIS > currentTimeMillis() }
                             val partitionedSessions = mySessions.partition { it.startsAt.toLongMillis() == session.startsAt.toLongMillis() }
                             val matchingSessions = partitionedSessions.first

                             if (matchingSessions.size == 1) {
                                 createReminderNotification(session.startsAt.toLongMillis(),
                                         "Upcoming Event in ${session.roomName}",
                                         "${session.title} is starting soon.")
                             } else {
                                 createReminderNotification(session.startsAt.toLongMillis(),
                                         "${matchingSessions.size} Upcoming Sessions",
                                         "You have ${matchingSessions.size} Sessions Starting soon")
                             }
                         } catch (e: NoSuchElementException){
                             print(e.message)
                         }
                     }
                     if (feedbackEnabled()) {
                         mySessions.forEach { session ->
                             if (session.feedbackRating == null) {
                                 createFeedbackNotification(session.endsAt.toLongMillis())
                             }
                         }
                     }
                 }
            }
        }
    }
}