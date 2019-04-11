package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.MySessions

expect fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int)

expect fun cancelLocalNotification(notificationId: Int)

expect fun initializeNotifications()

expect fun deinitializeNotifications()

expect fun showFeedbackAlert(session: MySessions)