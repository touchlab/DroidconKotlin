package co.touchlab.sessionize

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.Notification
import android.app.PendingIntent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import co.touchlab.sessionize.api.notificationFeedbackId
import co.touchlab.sessionize.api.notificationReminderId
import co.touchlab.sessionize.platform.AndroidAppContext
import co.touchlab.sessionize.platform.NotificationsModel

class NotificationPublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)

        notification?.let {
            with(NotificationManagerCompat.from(AndroidAppContext.app)) {
                // notificationId is a unique int for each notification that you must define
                this.notify(notificationId, it)
                Log.i(TAG, "Showing Local Notification $notificationId")
            }
            if (notificationId == notificationReminderId) {
                NotificationsModel.recreateReminderNotifications()
            }
            if (notificationId == notificationFeedbackId) {
                NotificationsModel.recreateFeedbackNotifications()
            }
        }
    }

    companion object {
        val TAG:String = NotificationPublisher::class.java.simpleName


        var NOTIFICATION_ID = "notification_id"
        var NOTIFICATION = "notification"
    }
}