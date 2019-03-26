package co.touchlab.sessionize.platform

expect fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int)

expect fun cancelLocalNotification(notificationId: Int)

expect fun initializeNotifications()

expect fun deinitializeNotifications()

val tenMinutesInMS:Int = 1000 * 10 * 60