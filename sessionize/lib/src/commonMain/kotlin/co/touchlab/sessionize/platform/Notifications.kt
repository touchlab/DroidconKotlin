package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.AppContext
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.api.notificationFeedbackTag
import co.touchlab.sessionize.api.notificationReminderTag
import com.russhwolf.settings.set


fun notificationsEnabled() : Boolean{
    return ServiceRegistry.appSettings.getBoolean(AppContext.LOCAL_NOTIFICATIONS_ENABLED,true)
}

fun feedbackEnabled() : Boolean{
    return ServiceRegistry.appSettings.getBoolean(AppContext.FEEDBACK_ENABLED,true)
}

fun reminderNotificationsEnabled() : Boolean{
    return ServiceRegistry.appSettings.getBoolean(AppContext.LOCAL_NOTIFICATIONS_ENABLED,true) &&
            ServiceRegistry.appSettings.getBoolean(AppContext.REMINDERS_ENABLED,true)
}

fun setNotificationsEnabled(enabled: Boolean){
    ServiceRegistry.appSettings[AppContext.LOCAL_NOTIFICATIONS_ENABLED] = enabled
}

fun setRemindersEnabled(enabled: Boolean){
    ServiceRegistry.appSettings[AppContext.REMINDERS_ENABLED] = enabled
}

fun setFeedbackEnabled(enabled: Boolean){
    ServiceRegistry.appSettings[AppContext.FEEDBACK_ENABLED] = enabled
}

fun createReminderNotificationsForSessions(sessions:List<MySessions>){
    if(reminderNotificationsEnabled() && notificationsEnabled()) {
        sessions.forEach { session ->
            val notificationTime = session.startsAt.toLongMillis() - AppContext.TEN_MINS_MILLIS
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

fun createFeedbackNotificationsForSessions(sessions:List<MySessions>){
    if (feedbackEnabled() && notificationsEnabled()) {
        sessions.forEach { session ->
            if(session.feedbackRating == null) {
                val feedbackNotificationTime = session.endsAt.toLongMillis() + AppContext.TEN_MINS_MILLIS
                ServiceRegistry.notificationsApi.createLocalNotification("How was the session?",
                        " Leave feedback for " + session.title,
                        feedbackNotificationTime,
                        //Not great. Possible to clash, although super unlikely
                        session.id.hashCode(),
                        notificationFeedbackTag)
            }
        }
    }
}

fun cancelReminderNotificationsForSessions(sessions:List<MySessions>){
    if(!reminderNotificationsEnabled() || !notificationsEnabled()) {
        sessions.forEach { session ->
            ServiceRegistry.notificationsApi.cancelLocalNotification(session.id.hashCode(), notificationReminderTag)
        }
    }
}

fun cancelFeedbackNotificationsForSessions(sessions:List<MySessions>){
    if(!feedbackEnabled() || !notificationsEnabled()) {
        sessions.forEach { session ->
            ServiceRegistry.notificationsApi.cancelLocalNotification(session.id.hashCode(), notificationFeedbackTag)
        }
    }
}