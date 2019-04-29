package co.touchlab.sessionize.api

interface NotificationsApi {

    fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int)

    fun cancelLocalNotification(notificationId: Int)

    fun initializeNotifications()

    fun deinitializeNotifications()

}