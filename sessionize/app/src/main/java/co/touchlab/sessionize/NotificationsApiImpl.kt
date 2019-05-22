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
import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.api.notificationReminderTag


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
        val intent = Intent().also { intent ->
            intent.action = AndroidAppContext.app.getString(R.string.notification_action)
            intent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationId)
            intent.putExtra(NotificationPublisher.NOTIFICATION_TAG, notificationTag)
            intent.putExtra(NotificationPublisher.NOTIFICATION, builder.build())
            val componentName = ComponentName(AndroidAppContext.app, NotificationPublisher::class.java)
            intent.component = componentName
        }

        val pendingIntent = PendingIntent.getBroadcast(AndroidAppContext.app, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        print("Local $notificationTag Notification Created at $timeInMS: $title - $message \n")

        // Scheduling Intent
        val alarmManager = AndroidAppContext.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMS, pendingIntent)
    }

    override fun cancelLocalNotification(notificationId: Int, notificationTag: String) {

        val alarmManager = AndroidAppContext.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent().also { intent ->
            intent.action = AndroidAppContext.app.getString(R.string.notification_action)
            intent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationId)
            intent.putExtra(NotificationPublisher.NOTIFICATION_TAG, notificationTag)
            val componentName = ComponentName(AndroidAppContext.app, NotificationPublisher::class.java)
            intent.component = componentName
        }
        val pendingIntent = PendingIntent.getBroadcast(AndroidAppContext.app, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        try {
            alarmManager.cancel(pendingIntent)
        }catch (e: RemoteException){
            print(e.localizedMessage)
        }


        with(NotificationManagerCompat.from(AndroidAppContext.app)) {
            this.cancel(notificationTag,notificationId)
            print("Cancelling Local $notificationTag Notification")

        }
    }

    override fun cancelAllNotifications(sessions:List<MySessions>){
        for(session in sessions) {
            val notificationId = session.startsAt.toLongMillis().hashCode()
            val alarmManager = AndroidAppContext.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent().also { intent ->
                intent.action = AndroidAppContext.app.getString(R.string.notification_action)
                intent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationId)
                intent.putExtra(NotificationPublisher.NOTIFICATION_TAG, notificationReminderTag)
                val componentName = ComponentName(AndroidAppContext.app, NotificationPublisher::class.java)
                intent.component = componentName
            }
            val pendingIntent = PendingIntent.getBroadcast(AndroidAppContext.app, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            try {
                alarmManager.cancel(pendingIntent)
            } catch (e: RemoteException) {
                print(e.localizedMessage)
            }
        }

        with(NotificationManagerCompat.from(AndroidAppContext.app)){
            this.cancelAll()
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
                print("Showing Local $notificationTag Notification")

            }
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
}