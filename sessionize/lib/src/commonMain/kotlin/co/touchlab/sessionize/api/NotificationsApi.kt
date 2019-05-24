package co.touchlab.sessionize.api

const val notificationFeedbackTag = "Feedback"
const val notificationReminderTag = "Reminder"
const val notificationFeedbackId = 1
const val notificationReminderId = 2

interface NotificationsApi {

    fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int, notificationTag: String)

    fun cancelLocalNotification(notificationId: Int, notificationTag: String)

    fun initializeNotifications(onSuccess: (Boolean) -> Unit)

    fun deinitializeNotifications()

}