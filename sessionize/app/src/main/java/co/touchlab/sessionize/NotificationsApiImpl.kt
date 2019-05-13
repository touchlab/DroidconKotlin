package co.touchlab.sessionize

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import app.sessionize.touchlab.lib.R
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.platform.AndroidAppContext
import co.touchlab.sessionize.platform.NotificationsModel.setNotificationsEnabled

class NotificationsApiImpl : NotificationsApi {

    override fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int, notificationTag: String) {

        print("Local $notificationTag Notification Created at $timeInMS: $title - $message \n")

        val pendingIntent = createPendingIntent(notificationId, notificationTag, NotificationPublisher.NOTIFICATION_ACTION_CREATE, title, message)
        val alarmManager = AndroidAppContext.app.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMS, pendingIntent)
    }

    override fun cancelLocalNotification(notificationId: Int, notificationTag: String, withDelay: Long?) {

        print("Local $notificationTag Notification Cancelled at $withDelay\n")

        val pendingIntent = createPendingIntent(notificationId, notificationTag, NotificationPublisher.NOTIFICATION_ACTION_DISMISS)
        val alarmManager = AndroidAppContext.app.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, withDelay ?: 0, pendingIntent)
    }

    private fun createPendingIntent(id:Int, tag: String, notificationAction:String, title:String? = "", message: String? = ""): PendingIntent{
        val intent = Intent(AndroidAppContext.app, NotificationPublisher::class.java).apply {
            action = notificationAction
            putExtra(NotificationPublisher.NOTIFICATION_TITLE,title)
            putExtra(NotificationPublisher.NOTIFICATION_MESSAGE,message)

            putExtra(NotificationPublisher.NOTIFICATION_ID, id)
            putExtra(NotificationPublisher.NOTIFICATION_TAG, tag)

            putExtra(NotificationPublisher.NOTIFICATION_CHANNEL_ID,AndroidAppContext.app.getString(R.string.notification_channel_id))
        }

        val uniqueId = id.toString() + tag + notificationAction
        return PendingIntent.getBroadcast(AndroidAppContext.app, uniqueId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }



    // General Notification Code

    override fun initializeNotifications(onSuccess: (Boolean) -> Unit)
    {
        createNotificationChannel()
        setNotificationsEnabled(true)
        onSuccess(true)
    }

    override fun deinitializeNotifications() {
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = AndroidAppContext.app.getString(R.string.notification_channel_name)
            val descriptionText = AndroidAppContext.app.getString(R.string.notification_channel_description)
            val channelId = AndroidAppContext.app.getString(R.string.notification_channel_id)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager = AndroidAppContext.app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}
