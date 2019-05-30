package co.touchlab.sessionize.api

const val notificationFeedbackId = 1
const val notificationReminderId = 2

interface NotificationsApi {

    fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int)

    fun cancelLocalNotification(notificationId: Int)

    fun initializeNotifications(onSuccess: (Boolean) -> Unit)

    fun deinitializeNotifications()

}