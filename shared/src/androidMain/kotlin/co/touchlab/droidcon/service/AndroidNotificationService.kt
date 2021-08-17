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
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalSettingsApi::class)
class AndroidNotificationService(
    private val context: Context,
    private val entrypointActivity: Class<out Activity>,
    private val log: Kermit,
    private val settings: ObservableSettings,
    private val json: Json,
): NotificationService {

    private var notificationIdCounter: Int
        get() = settings[NOTIFICATION_ID_COUNTER_KEY, 0]
        set(value) {
            settings[NOTIFICATION_ID_COUNTER_KEY] = value
        }

    private val registeredNotifications: MutableMap<Session.Id, List<Int>> = settings.getStringOrNull(NOTIFICATION_ID_MAP_KEY)?.let {
        json.decodeFromString(it)
    } ?: mutableMapOf()

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
    override suspend fun schedule(sessionId: Session.Id, title: String, body: String, delivery: Instant, dismiss: Instant?) {
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

        val intentId = nextNotificationId()
        val pendingIntent = createPendingIntent(intentId) {
            putExtra(NOTIFICATION_PAYLOAD_ID, intentId)
            putExtra(NOTIFICATION_PAYLOAD_TYPE, NOTIFICATION_TYPE_SHOW)
            putExtra(NOTIFICATION_PAYLOAD_NOTIFICATION, builder.build())
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, deliveryTime, pendingIntent)

        saveRegisteredNotificationId(sessionId, intentId)

        if (dismiss != null) {
            val dismissIntentId = nextNotificationId()
            val dismissPendingIntent = createPendingIntent(dismissIntentId) {
                putExtra(NOTIFICATION_PAYLOAD_ID, intentId)
                putExtra(NOTIFICATION_PAYLOAD_TYPE, NOTIFICATION_TYPE_DISMISS)
            }
            alarmManager.set(AlarmManager.RTC_WAKEUP, dismiss.toEpochMilliseconds(), dismissPendingIntent)

            saveRegisteredNotificationId(sessionId, dismissIntentId)
        }
    }

    override suspend fun cancel(sessionIds: List<Session.Id>) {
        if (sessionIds.isEmpty()) { return }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (sessionId in sessionIds) {
            val notificationIds = registeredNotifications[sessionId] ?: continue

            for (id in notificationIds) {
                try {
                    alarmManager.cancel(createPendingIntent(id))
                } catch (e: RemoteException) {
                    log.w(e) { "Couldn't cancel notification with ID '$id'." }
                }
            }

            deleteRegisteredNotificationIdSession(sessionId)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createPendingIntent(id: Int, intentTransform: Intent.() -> Unit = {}): PendingIntent {
        val intent = IdentifiableIntent("$id", context, NotificationPublisher::class.java).apply(intentTransform)
        return PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private fun nextNotificationId(): Int {
        val notificationId = notificationIdCounter
        notificationIdCounter++
        return notificationId
    }

    private fun saveRegisteredNotificationId(sessionId: Session.Id, notificationId: Int) {
        val currentNotificationIds = (registeredNotifications[sessionId] ?: emptyList()).toMutableList()
        currentNotificationIds.add(notificationId)
        registeredNotifications[sessionId] = currentNotificationIds
        saveRegisteredNotifications()
    }

    private fun deleteRegisteredNotificationIdSession(sessionId: Session.Id) {
        registeredNotifications.remove(sessionId)
        saveRegisteredNotifications()
    }

    private fun saveRegisteredNotifications() {
        settings[NOTIFICATION_ID_MAP_KEY] = json.encodeToString(registeredNotifications)
    }

    private data class NotificationIds(
        var reminderId: Int?,
        var dismissId: Int?,
        var feedbackId: Int?,
    )

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"

        const val NOTIFICATION_TYPE_SHOW = "SHOW"
        const val NOTIFICATION_TYPE_DISMISS = "DISMISS"

        const val NOTIFICATION_PAYLOAD_ID = "NOTIFICATION_PAYLOAD_ID"
        const val NOTIFICATION_PAYLOAD_TYPE = "NOTIFICATION_PAYLOAD_TYPE"
        const val NOTIFICATION_PAYLOAD_NOTIFICATION = "NOTIFICATION_PAYLOAD_NOTIFICATION"

        const val NOTIFICATION_ID_COUNTER_KEY = "NOTIFICATION_ID_COUNTER"
        const val NOTIFICATION_ID_MAP_KEY = "NOTIFICATION_ID_MAP"
    }
}