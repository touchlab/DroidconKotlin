package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.MySessions

val NotificationReminderTag = "REMINDER"
val NotificationFeedbackTag = "FEEDBACK"

expect fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int, notificationTag:String?)

expect fun cancelLocalNotification(notificationId: Int,notificationTag: String?)

expect fun initializeNotifications()

expect fun deinitializeNotifications()

expect fun showFeedbackAlert(session: MySessions)