package co.touchlab.sessionize

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import co.touchlab.sessionize.NotificationsApiImpl.Companion.notificationFeedbackId
import co.touchlab.sessionize.NotificationsApiImpl.Companion.notificationReminderId
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.platform.NotificationsModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.*


class NotificationPublisher : BroadcastReceiver(), KoinComponent {

    private val notificationsModel:NotificationsModel by inject()
    private val notificationsApi:NotificationsApi by inject()
    private val mainScope = MainScope()
    override fun onReceive(context: Context, intent: Intent) {
        val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        val notificationActionId = intent.getIntExtra(NOTIFICATION_ACTION_ID, 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(notificationActionId == 0){
          notification?.let {
              Log.i(TAG, "---OnReceive called, creating   ${NotificationsApiImpl.notificationIdToString(notificationId)} notification")
              with(NotificationManagerCompat.from(context)) {
                  this.notify(notificationId, notification)
              }

              mainScope.launch {
                  if (notificationId == notificationReminderId) {
                      notificationsModel.recreateReminderNotifications()

                      val currentTime = Calendar.getInstance().time
                      dismissLocalNotification(notificationId, currentTime.time + (Durations.TEN_MINS_MILLIS * 2))
                  }
                  if (notificationId == notificationFeedbackId) {
                      notificationsModel.recreateFeedbackNotifications()
                  }
              }
            }
        }
        else {
            Log.i(TAG, "---OnReceive called, dismissing ${NotificationsApiImpl.notificationIdToString(notificationActionId)} notification")
            notificationManager.cancel(notificationActionId)
        }
    }

    private fun dismissLocalNotification(notificationId: Int, withDelay: Long){
//        Log.i(TAG, "Dismissing ${NotificationsApiImpl.notificationIdToString(notificationId)} notification at ${notificationsApi.msTimeToString(withDelay)}(${withDelay}ms):")
        val context: Context = get()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = NotificationsApiImpl.createPendingIntent(NotificationsApiImpl.notificationDismissId, actionId = notificationId, context = context)
        alarmManager.set(AlarmManager.RTC_WAKEUP, withDelay, pendingIntent)
    }

    companion object {
        val TAG:String = NotificationPublisher::class.java.simpleName

        var NOTIFICATION_ID = "notification_id"
        var NOTIFICATION_ACTION_ID = "notification_action_id"
        var NOTIFICATION = "notification"
    }
}