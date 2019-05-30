package co.touchlab.sessionize

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.Notification
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import co.touchlab.sessionize.platform.AndroidAppContext

class NotificationPublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        val notificationTag = intent.getStringExtra(NOTIFICATION_TAG)

        with(NotificationManagerCompat.from(AndroidAppContext.app)) {
            // notificationId is a unique int for each notification that you must define
            this.notify(notificationTag, notificationId, notification)
            Log.i(TAG,"Showing Local Notification $notificationId")
        }
    }

    companion object {
        val TAG:String = NotificationPublisher::class.java.simpleName


        var NOTIFICATION_ID = "notification_id"
        var NOTIFICATION_TAG = "notification_tag"
        var NOTIFICATION = "notification"
    }
}