package co.touchlab.sessionize

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import app.sessionize.touchlab.lib.R
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.platform.AndroidAppContext
import co.touchlab.sessionize.platform.NotificationsModel.setNotificationsEnabled

class NotificationsApiImpl : NotificationsApi {

    override fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int, notificationTag: String) {

        Log.i("Notif","Local $notificationTag Notification Created at $timeInMS: $title - $message \n")

        val intent = Intent(AndroidAppContext.app, NotificationPublisher::class.java).apply {
            action = NotificationPublisher.NOTIFICATION_ACTION_CREATE

            putExtra(NotificationPublisher.NOTIFICATION_TITLE,title)
            putExtra(NotificationPublisher.NOTIFICATION_MESSAGE,message)

            putExtra(NotificationPublisher.NOTIFICATION_ID, notificationId)
            putExtra(NotificationPublisher.NOTIFICATION_TAG, notificationTag)

            putExtra(NotificationPublisher.NOTIFICATION_CHANNEL_ID,AndroidAppContext.app.getString(R.string.notification_channel_id))
            putExtra(NotificationPublisher.NOTIFICATION_MESSAGE,message)
        }

        val pendingIntent = PendingIntent.getBroadcast(AndroidAppContext.app, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = AndroidAppContext.app.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMS, pendingIntent)
    }

    override fun cancelLocalNotification(notificationId: Int, notificationTag: String, withDelay: Long?) {
        val intent = Intent(AndroidAppContext.app, NotificationPublisher::class.java).apply {
            action = NotificationPublisher.NOTIFICATION_ACTION_CREATE
        }
        val pendingIntent = PendingIntent.getBroadcast(AndroidAppContext.app, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = AndroidAppContext.app.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent!!)

        val notificationManager = AndroidAppContext.app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationTag, notificationId)
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
