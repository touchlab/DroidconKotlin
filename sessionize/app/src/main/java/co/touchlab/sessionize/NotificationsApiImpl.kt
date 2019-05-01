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
import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.api.notificationFeedbackTag
import co.touchlab.sessionize.api.notificationReminderTag
import co.touchlab.sessionize.platform.AndroidAppContext
import co.touchlab.sessionize.platform.AndroidAppContext.backgroundTask
import co.touchlab.sessionize.platform.currentTimeMillis
import co.touchlab.sessionize.platform.feedbackEnabled
import co.touchlab.sessionize.platform.notificationsEnabled
import co.touchlab.sessionize.platform.reminderNotificationsEnabled
import co.touchlab.sessionize.platform.setNotificationsEnabled


class NotificationsApiImpl : NotificationsApi {

    private val notificationPublisher: BroadcastReceiver = NotificationPublisher()

    override fun createReminderNotificationsForSessions(sessions:List<MySessions>){
        if(reminderNotificationsEnabled() && notificationsEnabled()) {
            sessions.forEach { session ->
                val notificationTime = session.startsAt.toLongMillis() - AppContext.TEN_MINS_MILLIS
                if (notificationTime > currentTimeMillis()) {
                    createLocalNotification("Upcoming Event in " + session.roomName,
                            session.title + " is starting soon.",
                            notificationTime,
                            session.id.hashCode(),
                            notificationReminderTag)
                }
            }
        }
    }

    override fun createFeedbackNotificationsForSessions(sessions:List<MySessions>){
        if (feedbackEnabled() && notificationsEnabled()) {
            sessions.forEach { session ->
                if(session.feedbackRating == null) {
                    val feedbackNotificationTime = session.endsAt.toLongMillis() + AppContext.TEN_MINS_MILLIS
                    createLocalNotification("How was the session?",
                            " Leave feedback for " + session.title,
                            feedbackNotificationTime,
                            //Not great. Possible to clash, although super unlikely
                            session.id.hashCode(),
                            notificationFeedbackTag)
                }
            }
        }
    }

    override fun cancelReminderNotificationsForSessions(sessions:List<MySessions>){
        if(!reminderNotificationsEnabled() || !notificationsEnabled()) {
            sessions.forEach { session ->
                cancelLocalNotification(session.id.hashCode(), notificationReminderTag)
            }
        }
    }

    override fun cancelFeedbackNotificationsForSessions(sessions:List<MySessions>){
        if(!feedbackEnabled() || !notificationsEnabled()) {
            sessions.forEach { session ->
                cancelLocalNotification(session.id.hashCode(), notificationFeedbackTag)
            }
        }
    }

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

        // Scheduling Intent
        val alarmManager = AndroidAppContext.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMS, pendingIntent)
    }

    override fun cancelLocalNotification(notificationId: Int, notificationTag: String) {
        with(NotificationManagerCompat.from(AndroidAppContext.app)) {
            this.cancel(notificationTag,notificationId)
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