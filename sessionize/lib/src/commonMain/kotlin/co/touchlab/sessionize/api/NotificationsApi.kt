package co.touchlab.sessionize.api

const val notificationFeedbackId = 1
const val notificationReminderId = 2
const val notificationDismissId = 3

interface NotificationsApi {

    fun scheduleLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int)

    fun dismissLocalNotification(notificationId: Int, withDelay: Long)

    fun cancelLocalNotification(notificationId: Int)

    fun initializeNotifications(onSuccess: (Boolean) -> Unit)

    fun deinitializeNotifications()

}