package co.touchlab.sessionize

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.sessionize.touchlab.lib.R
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.platform.AndroidAppContext
import co.touchlab.sessionize.platform.NotificationsModel.setNotificationsEnabled
import android.os.RemoteException
import android.util.Log
import co.touchlab.sessionize.platform.NotificationsModel


class NotificationsApiImpl : NotificationsApi {

    private val notificationPublisher: BroadcastReceiver = NotificationPublisher()

    override fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int, notificationTag: String) {
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
        val pendingIntent = createPendingIntent(notificationId, notificationTag, builder.build())
        Log.i(TAG, "Local $notificationTag Notification ${timeInMS.toInt()} Created at $timeInMS ms: $title - $message \n")

        // Scheduling Intent
        val alarmManager = AndroidAppContext.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMS, pendingIntent)
    }

    override fun cancelLocalNotification(notificationId: Int, notificationTag: String) {
        val alarmManager = AndroidAppContext.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(notificationId, notificationTag)
        try {
            alarmManager.cancel(pendingIntent)
            Log.i(TAG, "Cancelled Notification(1): $notificationId")
        } catch (e: RemoteException) {
            Log.i(TAG, e.localizedMessage)
        }
    }

    // General Notification Code

    override fun initializeNotifications(onSuccess: (Boolean) -> Unit)
    {
        val filter = IntentFilter(AndroidAppContext.app.getString(R.string.notification_action))
        AndroidAppContext.app.registerReceiver(notificationPublisher, filter)

        createNotificationChannel()
        setNotificationsEnabled(true)
        onSuccess(true)
    }

    override fun deinitializeNotifications() {
    }

    class NotificationPublisher : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)
            val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
            val notificationTag = intent.getStringExtra(NOTIFICATION_TAG)

            with(NotificationManagerCompat.from(AndroidAppContext.app)) {
                // notificationId is a unique int for each notification that you must define
                this.notify(notificationTag, notificationId, notification)
                Log.i(TAG,"Showing Local $notificationTag Notification")

            }
            NotificationsModel.recreateNotifications()
        }

        companion object {
            var NOTIFICATION_ID = "notification_id"
            var NOTIFICATION_TAG = "notification_tag"
            var NOTIFICATION = "notification"
        }
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


            if(!notificationManager.notificationChannels.contains(channel)) {
                notificationManager.createNotificationChannel(channel)
            }
        }
    }



    companion object{
        val TAG:String = NotificationsApiImpl::class.java.simpleName


        private fun createPendingIntent(id:Int, tag:String, notification:Notification? = null): PendingIntent{
            // Building Intent wrapper
            val intent = Intent().also { intent ->
                intent.action = AndroidAppContext.app.getString(R.string.notification_action)
                intent.putExtra(NotificationPublisher.NOTIFICATION_ID, id)
                intent.putExtra(NotificationPublisher.NOTIFICATION_TAG, tag)
                intent.putExtra(NotificationPublisher.NOTIFICATION, notification)
                val componentName = ComponentName(AndroidAppContext.app, NotificationPublisher::class.java)
                intent.component = componentName
            }
            return PendingIntent.getBroadcast(AndroidAppContext.app, id, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }
    }
}