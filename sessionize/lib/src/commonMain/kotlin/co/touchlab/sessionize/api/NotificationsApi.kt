package co.touchlab.sessionize.api

const val notificationFeedbackTag = "Feedback"
const val notificationReminderTag = "Reminder"

interface NotificationsApi {

    fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int, notificationTag: String)

    fun cancelLocalNotification(notificationId: Int, notificationTag: String)

    fun initializeNotifications()

    fun deinitializeNotifications()

}