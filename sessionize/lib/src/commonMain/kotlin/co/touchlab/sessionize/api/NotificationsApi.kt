package co.touchlab.sessionize.api

import co.touchlab.droidcon.db.MySessions

const val notificationFeedbackTag = "Feedback"
const val notificationReminderTag = "Reminder"

interface NotificationsApi {

    fun createReminderNotificationsForSessions(sessions:List<MySessions>)

    fun createFeedbackNotificationsForSessions(sessions:List<MySessions>)

    fun cancelReminderNotificationsForSessions(sessions:List<MySessions>)

    fun cancelFeedbackNotificationsForSessions(sessions:List<MySessions>)


    // General Notification Code

    fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int, notificationTag: String)

    fun cancelLocalNotification(notificationId: Int, notificationTag: String)

    fun initializeNotifications(onSuccess: (Boolean) -> Unit)

    fun deinitializeNotifications()

}