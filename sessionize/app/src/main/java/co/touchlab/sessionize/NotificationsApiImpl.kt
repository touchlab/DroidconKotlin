package co.touchlab.sessionize

import android.app.Activity
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import app.sessionize.touchlab.lib.R
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.platform.AndroidAppContext
import co.touchlab.sessionize.platform.NotificationsModel.setNotificationsEnabled
import android.os.RemoteException
import android.util.Log
import androidx.core.app.NotificationCompat
import co.touchlab.sessionize.api.notificationDismissId
import co.touchlab.sessionize.api.notificationReminderId

class NotificationsApiImpl : NotificationsApi {

    override fun scheduleLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int) {
        Log.i(TAG, "Creating   ${if(notificationId == notificationReminderId) "reminder" else "feedback"} notification at $timeInMS ms: $title - $message")

        // Building Notification
        val channelId = AndroidAppContext.app.getString(R.string.notification_channel_id)
        val builder = NotificationCompat.Builder(AndroidAppContext.app, channelId)
                .setSmallIcon(R.drawable.baseline_insert_invitation_24)
                .setContentTitle(title)
                .setContentText(message)
                .setWhen(timeInMS)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)

        // Building Intent wrapper
        val pendingIntent = createPendingIntent(notificationId, notification = builder.build())
        val alarmManager = AndroidAppContext.app.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMS, pendingIntent)
    }

    override fun dismissLocalNotification(notificationId: Int, withDelay: Long){
        Log.i(TAG, "Dismissing ${if (notificationId == notificationReminderId) "reminder" else "feedback"} notification at $withDelay ms")
        val alarmManager = AndroidAppContext.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(notificationDismissId, actionId = notificationId)
        alarmManager.set(AlarmManager.RTC_WAKEUP, withDelay, pendingIntent)
    }


    override fun cancelLocalNotification(notificationId: Int) {
        Log.i(TAG, "Cancelling ${if (notificationId == notificationReminderId) "reminder" else "feedback"} notification, alarm only")
        val alarmManager = AndroidAppContext.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(notificationId)
        alarmManager.cancel(pendingIntent)
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

    companion object{
        val TAG:String = NotificationsApiImpl::class.java.simpleName


        fun createPendingIntent(id:Int, actionId:Int? = null, notification: Notification? = null): PendingIntent{
            // Building Intent wrapper
            val intent = Intent(AndroidAppContext.app,NotificationPublisher::class.java).apply {
                putExtra(NotificationPublisher.NOTIFICATION_ID, id)
                putExtra(NotificationPublisher.NOTIFICATION_ACTION_ID, actionId)
                putExtra(NotificationPublisher.NOTIFICATION, notification)
            }
            return PendingIntent.getBroadcast(AndroidAppContext.app, id, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }
    }
}
