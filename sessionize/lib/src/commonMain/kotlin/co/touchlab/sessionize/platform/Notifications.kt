package co.touchlab.sessionize.platform

expect fun createLocalNotificationOnPlatform(title:String, message:String, timeInMS:Long, notificationId: Int)

expect fun cancelLocalNotificationOnPlatform(notificationId: Int)

expect fun initializeNotificationsOnPlatform()

expect fun deinitializeNotificationsOnPlatform()