package co.touchlab.sessionize.platform

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

    val notificationInfo = mutableListOf<Pair<Int,String>>()

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
        recreateReminderNotifications()
        recreateFeedbackNotifications()
    }

    fun cancelNotificationsForSessions() {
        if (!notificationsEnabled() || !reminderNotificationsEnabled() || !feedbackEnabled()) {
            cancelAllNotifications(notificationReminderTag)
            cancelFeedbackNotification()
        }
    }

    // create Singular

    private fun createReminderNotification(startsAtTime: Long, title:String, message:String){
        val notificationTime = startsAtTime - Durations.TEN_MINS_MILLIS
        if (notificationTime > currentTimeMillis()) {
            notificationInfo.add(Pair(notificationTime.toInt(), notificationReminderTag))
            ServiceRegistry.notificationsApi.createLocalNotification(title,
                                                                    message,
                                                                    notificationTime,
                                                                    notificationTime.toInt(),
                                                                    notificationReminderTag)
        }
    }

    private fun createFeedbackNotification(endsAtTime: Long){
        val feedbackNotificationTime = endsAtTime + Durations.TEN_MINS_MILLIS
        notificationInfo.add(Pair(feedbackNotificationTime.toInt(), notificationFeedbackTag))
        ServiceRegistry.notificationsApi.createLocalNotification("Feedback Time!",
                "Your Feedback is Requested",
                feedbackNotificationTime,
                feedbackNotificationTime.toInt(),
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
        cancelAllNotifications(notificationFeedbackTag)
    }

    fun recreateReminderNotifications(){
        cancelAllNotifications(notificationReminderTag)

        if (notificationsEnabled()){
             backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
                 if(mySessions.isNotEmpty()) {
                     if (reminderNotificationsEnabled()) {

                         try {
                             mySessions.groupBy { it.startsAt.toLongMillis() }.forEach {
                                 if (it.value.size == 1) {
                                     val session = it.value.first()
                                     createReminderNotification(it.key,
                                             "Upcoming Event in ${session.roomName}",
                                             "${session.title} is starting soon.")
                                 } else {
                                     createReminderNotification(it.key,
                                             "${it.value.size} Upcoming Sessions",
                                             "You have ${it.value.size} Sessions Starting soon")
                                 }
                             }

                         } catch (e: NoSuchElementException){
                             print(e.message)
                         }
                     }
                 }
            }
        }
    }

    fun recreateFeedbackNotifications(){
        cancelFeedbackNotification()

        if (notificationsEnabled()){
            backgroundTask({ sessionQueries.mySessions().executeAsList() }) { mySessions ->
                if(mySessions.isNotEmpty()) {
                    if (feedbackEnabled()) {

                        try {
                            mySessions.filter{ it.feedbackRating == null }.groupBy{ it.endsAt.toLongMillis() }.forEach {
                                createFeedbackNotification(it.key + Durations.TEN_MINS_MILLIS)
                            }

                        } catch (e: NoSuchElementException){
                            print(e.message)
                        }


                        try {
                            val session = mySessions.first { (it.endsAt.toLongMillis() + Durations.TEN_MINS_MILLIS > currentTimeMillis())}
                        } catch (e: NoSuchElementException){
                            print(e.message)
                        }
                    }
                }
            }
        }
    }

    private fun cancelAllNotifications(notificationTag:String?){

        var list: List<Pair<Int,String>> = notificationInfo
        notificationTag?.let { tag ->
            list = notificationInfo.partition { it.second == tag }.first
        }

        list.forEach {
            ServiceRegistry.notificationsApi.cancelLocalNotification(it.first,it.second)
        }
        notificationInfo.removeAll(list)
    }
}