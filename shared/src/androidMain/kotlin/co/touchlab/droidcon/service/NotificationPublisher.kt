package co.touchlab.droidcon.service

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationPublisher: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(AndroidNotificationService.NOTIFICATION_PAYLOAD_ID, 0)
        val notificationType = intent.getStringExtra(AndroidNotificationService.NOTIFICATION_PAYLOAD_TYPE)
        val notification = intent.getParcelableExtra<Notification>(AndroidNotificationService.NOTIFICATION_PAYLOAD_NOTIFICATION)

        with(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
            if (notificationType == AndroidNotificationService.NOTIFICATION_TYPE_DISMISS) {
                cancel(notificationId)
            } else {
                notify(notificationId, notification)
            }
        }
    }
}