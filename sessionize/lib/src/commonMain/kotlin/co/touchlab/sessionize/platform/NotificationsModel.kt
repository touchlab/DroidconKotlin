package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.Durations
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.LOCAL_NOTIFICATIONS_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.api.notificationFeedbackId
import co.touchlab.sessionize.api.notificationReminderId
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
        cancelReminderNotifications()
        cancelFeedbackNotifications()
    }

    fun cancelFeedbackNotifications(){
        ServiceRegistry.notificationsApi.cancelLocalNotification(notificationFeedbackId)
    }

    fun cancelReminderNotifications(){
        ServiceRegistry.notificationsApi.cancelLocalNotification(notificationReminderId)
    }

    // Reminder
    fun recreateReminderNotifications() {
        if (remindersEnabled()){
            backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
                if(mySessions.isNotEmpty()) {
                    try {
                        val session = mySessions.first { it.startsAt.toLongMillis() - Durations.TEN_MINS_MILLIS > currentTimeMillis() }
                        val partitionedSessions = mySessions.partition { it.startsAt.toLongMillis() == session.startsAt.toLongMillis() }
                        val sessionGroup = partitionedSessions.first
                        scheduleReminderForSessionGroup(sessionGroup)
                        scheduleDismissalForSessionGroup(sessionGroup)

                    } catch (e: NoSuchElementException) {
                        print(e.message)
                    }
                }
            }
        }
    }

    // Schedule Reminder
    private fun scheduleReminderForSessionGroup(sessions:List<MySessions>){
        print("scheduleReminderForSessionGroup\n")

        val firstSession = sessions.first()
        val reminderTime = firstSession.startsAt.toLongMillis() - Durations.TEN_MINS_MILLIS

        if (sessions.size == 1) {
            ServiceRegistry.notificationsApi.scheduleLocalNotification(
                    "Upcoming Event in ${firstSession.roomName}",
                    "${firstSession.title} is starting soon.",
                    reminderTime,
                    notificationReminderId)
        } else {
            ServiceRegistry.notificationsApi.scheduleLocalNotification(
                    "${sessions.size} Upcoming Sessions",
                    "You have ${sessions.size} Sessions Starting soon",
                    reminderTime,
                    notificationReminderId)
        }
    }

    // Schedule Cancel
    private fun scheduleDismissalForSessionGroup(sessions:List<MySessions>){
        print("scheduleDismissalForSessionGroup\n")

        val firstSession = sessions.first()
        val dismissalTime = firstSession.startsAt.toLongMillis() + Durations.TEN_MINS_MILLIS

        ServiceRegistry.notificationsApi.dismissLocalNotification(notificationReminderId, dismissalTime)
    }


    // Feedback
    fun recreateFeedbackNotifications() {
        if (feedbackEnabled()){
            backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
                if(mySessions.isNotEmpty()) {
                    try {
                        val mySession = mySessions.first { (it.endsAt.toLongMillis() + Durations.TEN_MINS_MILLIS > currentTimeMillis())}
                        scheduleFeedbackForSession(mySession)
                    } catch (e: NoSuchElementException) {
                        print(e.message)
                    }
                }
            }
        }
    }

    // Schedule Feedback
    private fun scheduleFeedbackForSession(sessions:MySessions){
        print("scheduleFeedbackForSession\n")
        val feedbackTime = sessions.endsAt.toLongMillis() + Durations.TEN_MINS_MILLIS
        ServiceRegistry.notificationsApi.scheduleLocalNotification("Feedback Time!",
                "Your Feedback is Requested",
                feedbackTime,
                notificationFeedbackId)
    }

}