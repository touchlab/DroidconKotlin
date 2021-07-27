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
import android.os.RemoteException
import android.util.Log
import androidx.core.app.NotificationCompat
import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.platform.currentTimeMillis
import co.touchlab.sessionize.platform.notificationsEnabled
import com.russhwolf.settings.Settings
import java.text.SimpleDateFormat
import java.util.*
import kotlin.NoSuchElementException


class NotificationsApiImpl(
    private val appSettings: Settings,
    private val timeZone: String,
    private val context: Context
) :
    NotificationsApi {

    override fun scheduleReminderNotificationsForSessions(sessions: List<MySessions>) {
        try {
            val keySession = sessions.first { getReminderTimeFromSession(it) > currentTimeMillis() }
            val partitionedSessions =
                sessions.partition { it.startsAt.toLongMillis() == keySession.startsAt.toLongMillis() }
            val sessionGroup = partitionedSessions.first
            scheduleReminderForSessionGroup(sessionGroup)

        } catch (e: NoSuchElementException) {
            e.message?.let { Log.i(TAG, it) }
        }
    }

    private fun scheduleReminderForSessionGroup(sessions: List<MySessions>) {
        val firstSession = sessions.first()
        val reminderTime = getReminderTimeFromSession(firstSession)

        if (sessions.size == 1) {
            scheduleLocalNotification(
                getReminderNotificationTitle(firstSession),
                getReminderNotificationMessage(firstSession),
                reminderTime,
                notificationReminderId
            )
        } else {
            scheduleLocalNotification(
                "${sessions.size} Upcoming Sessions",
                "You have ${sessions.size} Sessions Starting soon",
                reminderTime,
                notificationReminderId
            )
        }
    }


    fun getReminderTimeFromSession(session: MySessions): Long =
        session.startsAt.toLongMillis() - Durations.TEN_MINS_MILLIS

    fun getReminderNotificationTitle(session: MySessions) = "Upcoming Event in ${session.roomName}"
    fun getReminderNotificationMessage(session: MySessions) = "${session.title} is starting soon."
    fun getFeedbackTimeFromSession(session: MySessions): Long =
        session.endsAt.toLongMillis() + Durations.TEN_MINS_MILLIS

    fun getFeedbackNotificationTitle() = "Feedback Time!"
    fun getFeedbackNotificationMessage() = "Your Feedback is Requested"

    override fun scheduleFeedbackNotificationsForSessions(sessions: List<MySessions>) {
        try {
            val session = sessions.first { (getFeedbackTimeFromSession(it) > currentTimeMillis()) }
            val feedbackTime = getFeedbackTimeFromSession(session)
            scheduleLocalNotification(
                getFeedbackNotificationTitle(),
                getFeedbackNotificationMessage(),
                feedbackTime,
                notificationFeedbackId
            )

        } catch (e: NoSuchElementException) {
            e.message?.let { Log.e(TAG, it) }
        }
    }

    override fun cancelReminderNotifications(andDismissals: Boolean) {
        cancelLocalNotification(notificationReminderId)
        if (andDismissals)
            cancelLocalNotification(notificationDismissId)
    }

    override fun cancelFeedbackNotifications() {
        cancelLocalNotification(notificationFeedbackId)
    }


    private fun scheduleLocalNotification(
        title: String,
        message: String,
        timeInMS: Long,
        notificationId: Int
    ) {
        /*Log.i(
            TAG,
            "Creating   ${notificationIdToString(notificationId)} notification at ${
                msTimeToString(timeInMS)
            }(${timeInMS}ms): $title - $message"
        )*/

        // Building Notification
        val channelId = context.getString(R.string.notification_channel_id)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_insert_invitation_24)
            .setContentTitle(title)
            .setContentText(message)
            .setWhen(timeInMS)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)

        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder.setContentIntent(contentIntent)

        // Building Intent wrapper
        val pendingIntent = createPendingIntent(notificationId, notification = builder.build(), context = context)
        val alarmManager =
            context.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMS, pendingIntent)
    }

    private fun cancelLocalNotification(notificationId: Int) {
        Log.i(TAG, "Cancelling ${notificationIdToString(notificationId)} notification, alarm only")
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(notificationId, context = context)
        try {
            alarmManager.cancel(pendingIntent)
        } catch (e: RemoteException) {
            Log.i(TAG, e.localizedMessage)
        }
    }

    override fun initializeNotifications(onSuccess: (Boolean) -> Unit) {
        createNotificationChannel()
        appSettings.notificationsEnabled = true
        onSuccess(true)
    }

    override fun deinitializeNotifications() {
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText =
                context.getString(R.string.notification_channel_description)
            val channelId = context.getString(R.string.notification_channel_id)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    /*override fun msTimeToString(time: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.timeZone = TimeZone.getTimeZone(timeZone)
        val format = SimpleDateFormat("MM/dd/YYYY, hh:mma")
        return format.format(calendar.time)
    }*/

    companion object {
        val TAG: String = NotificationsApiImpl::class.java.simpleName
        const val notificationFeedbackId = 1
        const val notificationReminderId = 2
        const val notificationDismissId = 3

        fun notificationIdToString(id: Int): String = when (id) {
            notificationReminderId -> "Reminder"
            notificationFeedbackId -> "Feedback"
            notificationDismissId -> "Dismiss"
            else -> ""
        }

        fun createPendingIntent(
            id: Int,
            actionId: Int? = null,
            notification: Notification? = null,
            context: Context
        ): PendingIntent {
            // Building Intent wrapper
            val intent = Intent(context, NotificationPublisher::class.java).apply {
                putExtra(NotificationPublisher.NOTIFICATION_ID, id)
                putExtra(NotificationPublisher.NOTIFICATION_ACTION_ID, actionId)
                putExtra(NotificationPublisher.NOTIFICATION, notification)
            }
            return PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
    }
}
