package co.touchlab.droidcon.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.RemoteException
import androidx.core.app.NotificationCompat
import co.touchlab.droidcon.R
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.util.IdentifiableIntent
import co.touchlab.kermit.Kermit
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlin.time.ExperimentalTime

class AndroidNotificationService(
    private val context: Context,
    private val entrypointActivity: Class<out Activity>,
    private val log: Kermit,
): NotificationService {
    override suspend fun initialize(): Boolean {
        log.v { "Initializing." }

        // Create notification channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = context.getString(R.string.notification_channel_description)
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
            log.d { "Notification channel created." }
        }

        return true
    }

    // FIXME: Try this on Android 12 before release. Bug: https://issuetracker.google.com/issues/180884673
    @SuppressLint("UnspecifiedImmutableFlag")
    @ExperimentalTime
    override suspend fun schedule(sessionId: Session.Id, title: String, body: String, delivery: Instant) {
        log.v { "Scheduling local notification at $delivery." }
        val deliveryTime = delivery.toEpochMilliseconds()

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_insert_invitation_24)
            .setContentTitle(title)
            .setContentText(body)
            .setWhen(deliveryTime)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)

        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, entrypointActivity),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(contentIntent)

        val alarmManager = context.getSystemService(Activity.ALARM_SERVICE) as AlarmManager

        val intentId = sessionId.value.hashCode()
        val pendingIntent = createPendingIntent(intentId, NOTIFICATION_TAG_REMINDER) {
            putExtra(NOTIFICATION_PAYLOAD_ID, intentId)
            putExtra(NOTIFICATION_PAYLOAD_TAG, NOTIFICATION_TAG_REMINDER)
            putExtra(NOTIFICATION_PAYLOAD_NOTIFICATION, builder.build())
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, deliveryTime, pendingIntent)

        // Create an automatic dismiss notification for reminders.
        val dismissDeliveryTime = delivery
            .plus(NotificationSchedulingService.REMINDER_DISMISS_OFFSET, DateTimeUnit.MINUTE)
            .toEpochMilliseconds()
        // Hash the ID again to get another ID.
        val dismissIntentId = intentId.toString().hashCode()
        val dismissPendingIntent = createPendingIntent(dismissIntentId, NOTIFICATION_TAG_DISMISS) {
            putExtra(NOTIFICATION_PAYLOAD_ID, intentId)
            putExtra(NOTIFICATION_PAYLOAD_TAG, NOTIFICATION_TAG_DISMISS)
            putExtra(NOTIFICATION_PAYLOAD_TARGET_TAG, NOTIFICATION_TAG_REMINDER)
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dismissDeliveryTime, dismissPendingIntent)
    }

    override suspend fun cancel(sessionIds: List<Session.Id>) {
        for (sessionId in sessionIds) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val pendingIntent = createPendingIntent(sessionId.value.hashCode(), NOTIFICATION_TAG_REMINDER)

            try {
                alarmManager.cancel(pendingIntent)
            } catch (e: RemoteException) {
                log.i { e.localizedMessage ?: "Unknown error occurred when cancelling notification with ID '${sessionId.value}'." }
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createPendingIntent(id: Int, tag: String, intentTransformation: Intent.() -> Unit = {}): PendingIntent {
        val intent = IdentifiableIntent("$id-$tag", context, NotificationPublisher::class.java).apply(intentTransformation)
        return PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_ONE_SHOT,
        )
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"

        const val NOTIFICATION_TAG_REMINDER = "REMINDER"
        const val NOTIFICATION_TAG_FEEDBACK = "FEEDBACK"
        const val NOTIFICATION_TAG_DISMISS = "DISMISS"

        const val NOTIFICATION_PAYLOAD_ID = "NOTIFICATION_PAYLOAD_ID"
        const val NOTIFICATION_PAYLOAD_TAG = "NOTIFICATION_PAYLOAD_TAG"
        const val NOTIFICATION_PAYLOAD_TARGET_TAG = "NOTIFICATION_PAYLOAD_TARGET_TAG"
        const val NOTIFICATION_PAYLOAD_NOTIFICATION = "NOTIFICATION_PAYLOAD_NOTIFICATION"
    }
}