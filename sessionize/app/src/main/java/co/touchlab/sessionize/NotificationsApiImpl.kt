package co.touchlab.sessionize

import android.app.Activity
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.platform.NotificationsModel.getFeedbackNotificationMessage
import co.touchlab.sessionize.platform.NotificationsModel.getFeedbackNotificationTitle
import co.touchlab.sessionize.platform.NotificationsModel.getFeedbackTimeFromSession
import co.touchlab.sessionize.platform.NotificationsModel.getReminderNotificationMessage
import co.touchlab.sessionize.platform.NotificationsModel.getReminderNotificationTitle
import co.touchlab.sessionize.platform.NotificationsModel.getReminderTimeFromSession
import co.touchlab.sessionize.platform.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.*
import kotlin.NoSuchElementException


class NotificationsApiImpl : NotificationsApi {

    override fun scheduleReminderNotificationsForSessions(sessions:List<MySessions>){
        try {
            val keySession = sessions.first { getReminderTimeFromSession(it) > currentTimeMillis() }
            val partitionedSessions = sessions.partition { it.startsAt.toLongMillis() == keySession.startsAt.toLongMillis() }
            val sessionGroup = partitionedSessions.first
            scheduleReminderForSessionGroup(sessionGroup)

        } catch (e: NoSuchElementException) {
            Log.i(TAG,e.message)
        }
    }

    private fun scheduleReminderForSessionGroup(sessions:List<MySessions>){
        //Log.i(TAG,"scheduleReminderForSessionGroup\n")

        val firstSession = sessions.first()
        val reminderTime = getReminderTimeFromSession(firstSession)

        if (sessions.size == 1) {
            scheduleLocalNotification(
                    getReminderNotificationTitle(firstSession),
                    getReminderNotificationMessage(firstSession),
                    reminderTime,
                    notificationReminderId)
        } else {
            scheduleLocalNotification(
                    "${sessions.size} Upcoming Sessions",
                    "You have ${sessions.size} Sessions Starting soon",
                    reminderTime,
                    notificationReminderId)
        }
    }




    override fun scheduleFeedbackNotificationsForSessions(sessions:List<MySessions>){
        //Log.i(TAG,"scheduleFeedbackForSession\n")
        try {
            val session = sessions.first { (getFeedbackTimeFromSession(it) > currentTimeMillis())}
            val feedbackTime = getFeedbackTimeFromSession(session)
            scheduleLocalNotification(getFeedbackNotificationTitle(),
                    getFeedbackNotificationMessage(),
                    feedbackTime,
                    notificationFeedbackId)

        } catch (e: NoSuchElementException) {
            Log.e(TAG,e.message)
        }
    }

    override fun cancelReminderNotifications(andDismissals: Boolean) {
        cancelLocalNotification(notificationReminderId)
        if(andDismissals)
            cancelLocalNotification(notificationDismissId)
    }

    override fun cancelFeedbackNotifications(){
        cancelLocalNotification(notificationFeedbackId)
    }



    private fun scheduleLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int) {
        Log.i(TAG, "Creating   ${notificationIdToString(notificationId)} notification at ${msTimeToString(timeInMS)}(${timeInMS}ms): $title - $message")

        // Building Notification
        val channelId = AndroidAppContext.app.getString(R.string.notification_channel_id)
        val builder = NotificationCompat.Builder(AndroidAppContext.app, channelId)
                .setSmallIcon(R.drawable.baseline_insert_invitation_24)
                .setContentTitle(title)
                .setContentText(message)
                .setWhen(timeInMS)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)

        builder.setContentIntent(contentIntent)

        // Building Intent wrapper
        val pendingIntent = createPendingIntent(notificationId, notification = builder.build())
        val alarmManager = AndroidAppContext.app.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMS, pendingIntent)
    }

    private fun cancelLocalNotification(notificationId: Int) {
        Log.i(TAG, "Cancelling ${notificationIdToString(notificationId)} notification, alarm only")
        val alarmManager = AndroidAppContext.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(notificationId)
        try {
            alarmManager.cancel(pendingIntent)
        } catch (e: RemoteException) {
            Log.i(TAG, e.localizedMessage)
        }
    }

    override suspend fun initializeNotifications(onSuccess: suspend (Boolean) -> Unit)
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
        const val notificationFeedbackId = 1
        const val notificationReminderId = 2
        const val notificationDismissId = 3

        fun notificationIdToString(id: Int) :String = when (id) {
            notificationReminderId -> "Reminder"
            notificationFeedbackId -> "Feedback"
            notificationDismissId ->  "Dismiss"
            else -> ""
        }

        fun msTimeToString(time:Long): String{
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time
            calendar.timeZone = TimeZone.getTimeZone(ServiceRegistry.timeZone)
            val format = SimpleDateFormat("MM/dd/YYYY, hh:mma")
            return format.format(calendar.time)
        }

        val contentIntent = PendingIntent.getActivity(AndroidAppContext.app, 0, Intent(AndroidAppContext.app, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

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
