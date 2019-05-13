package co.touchlab.sessionize

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.Notification
import android.os.Build
import android.util.Log

class NotificationPublisher : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = "notificationId"
        const val NOTIFICATION_TAG = "notificationTag"

        const val NOTIFICATION_TITLE = "notificationTitle"
        const val NOTIFICATION_MESSAGE = "notificationMessage"
        const val NOTIFICATION_CHANNEL_ID = "notificationChannelID"

        const val NOTIFICATION_ACTION_CREATE = "notificationCreate"
        const val NOTIFICATION_ACTION_DISMISS = "notificationDismiss"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            intent?.let {

                val id = intent.getIntExtra(NOTIFICATION_ID, -1)
                val tag = intent.getStringExtra(NOTIFICATION_TAG)

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if(intent.action == NOTIFICATION_ACTION_CREATE){
                    val title = intent.getStringExtra(NOTIFICATION_TITLE)
                    val message = intent.getStringExtra(NOTIFICATION_MESSAGE)
                    val channel = intent.getStringExtra(NOTIFICATION_CHANNEL_ID)
                    val notification = getNotification(context, title, message, channel)

                    notificationManager.notify(tag, id, notification)
                }
                else if(intent.action == NOTIFICATION_ACTION_DISMISS){
                    notificationManager.cancel(tag, id)
                }
            }
        }
    }

    private fun getNotification(context: Context, title: String, message: String, channelId: String): Notification {

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }
        builder.setContentTitle(title)
        builder.setContentText(message)
        builder.setSmallIcon(R.drawable.menu_schedule)
        return builder.build()
    }
}