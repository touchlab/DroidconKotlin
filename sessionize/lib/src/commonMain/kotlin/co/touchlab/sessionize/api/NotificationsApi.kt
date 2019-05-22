package co.touchlab.sessionize.api

import co.touchlab.droidcon.db.MySessions

const val notificationFeedbackTag = "Feedback"
const val notificationReminderTag = "Reminder"

interface NotificationsApi {

    fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int, notificationTag: String)

    fun cancelLocalNotification(notificationId: Int, notificationTag: String)

    fun cancelAllNotifications(sessions:List<MySessions>)

    fun initializeNotifications(onSuccess: (Boolean) -> Unit)

    fun deinitializeNotifications()

}