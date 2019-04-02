package co.touchlab.sessionize.api

import co.touchlab.sessionize.platform.createLocalNotificationOnPlatform
import co.touchlab.sessionize.platform.cancelLocalNotificationOnPlatform
import co.touchlab.sessionize.platform.initializeNotificationsOnPlatform
import co.touchlab.sessionize.platform.deinitializeNotificationsOnPlatform

object NotificationsApiImpl : NotificationsApi {

    override fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int) {
        createLocalNotificationOnPlatform(title,message,timeInMS,notificationId)
    }

    override fun cancelLocalNotification(notificationId: Int) {
        cancelLocalNotificationOnPlatform(notificationId)
    }

    override fun initializeNotifications() {
        initializeNotificationsOnPlatform()
    }

    override fun deinitializeNotifications() {
        deinitializeNotificationsOnPlatform()
    }

}