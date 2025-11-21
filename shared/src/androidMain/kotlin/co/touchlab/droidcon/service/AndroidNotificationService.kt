package co.touchlab.droidcon.service

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
import co.touchlab.droidcon.application.service.Notification
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.shared.R
import co.touchlab.droidcon.util.IdentifiableIntent
import co.touchlab.kermit.Logger
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlin.time.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AndroidNotificationService(
    private val context: Context,
    private val entrypointActivity: Class<out Activity>,
    private val log: Logger,
    private val syncService: SyncService,
    private val conferenceRepository: ConferenceRepository,
    private val settings: ObservableSettings,
    private val json: Json,
) : NotificationService {

    private var notificationIdCounter: Int
        get() = settings[NOTIFICATION_ID_COUNTER_KEY, 0]
        set(value) {
            settings[NOTIFICATION_ID_COUNTER_KEY] = value
        }

    private val registeredNotifications: MutableMap<String, List<Int>> = settings.getStringOrNull(NOTIFICATION_ID_MAP_KEY)?.let {
        json.decodeFromString(it)
    } ?: mutableMapOf()

    // TODO: Not called on Android.
    private var notificationHandler: DeepLinkNotificationHandler? = null

    override fun setHandler(notificationHandler: DeepLinkNotificationHandler) {
        this.notificationHandler = notificationHandler
    }

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

    override suspend fun schedule(notification: Notification.Local, title: String, body: String, delivery: Instant, dismiss: Instant?) {
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

        val requestCode = when (notification) {
            is Notification.Local.Feedback -> NOTIFICATION_FEEDBACK_REQUEST_CODE
            is Notification.Local.Reminder -> 0
        }

        val sessionId = when (notification) {
            is Notification.Local.Feedback -> notification.sessionId
            is Notification.Local.Reminder -> notification.sessionId
        }

        val typeValue = when (notification) {
            is Notification.Local.Feedback -> Notification.Values.FEEDBACK_TYPE
            is Notification.Local.Reminder -> Notification.Values.REMINDER_TYPE
        }

        val contentIntent = PendingIntent.getActivity(
            context,
            requestCode,
            Intent(context, entrypointActivity).apply {
                putExtra(Notification.Keys.SESSION_ID, sessionId.value)
                putExtra(Notification.Keys.NOTIFICATION_TYPE, typeValue)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            },
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
        if (sessionIds.isEmpty()) {
            return
        }

        log.v { "Cancelling scheduled notifications with IDs: [${sessionIds.joinToString { it.value }}]" }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (sessionId in sessionIds) {
            val notificationIds = registeredNotifications[sessionId.value] ?: continue

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

    suspend fun handleNotificationDeeplink(intent: Intent) {
        val notification = intent.parseNotification() ?: return

        handleNotification(notification)
    }

    suspend fun handleNotification(notification: Notification) {
        when (notification) {
            is Notification.DeepLink -> {
                val notificationHandler = notificationHandler
                if (notificationHandler != null) {
                    notificationHandler.handleDeepLinkNotification(notification)
                } else {
                    log.w { "notificationHandler not registered when received $notification" }
                }
            }

            Notification.Remote.RefreshData -> syncService.forceSynchronize(conferenceRepository.getSelected())
        }
    }

    private fun Intent.parseNotification(): Notification? =
        when (val typeValue = this.getStringExtra(Notification.Keys.NOTIFICATION_TYPE)) {
            Notification.Values.REMINDER_TYPE -> this.parseReminderNotification()
            Notification.Values.FEEDBACK_TYPE -> this.parseFeedbackNotification()
            Notification.Values.REFRESH_DATA_TYPE -> Notification.Remote.RefreshData
            // Expected on Android as this could've been just a regular app open without a notification.
            null -> null
            else -> {
                log.e { "Unknown notification type <$typeValue>, ignoring." }
                null
            }
        }

    private fun Intent.parseReminderNotification(): Notification.Local.Reminder? {
        val sessionId = this.getStringExtra(Notification.Keys.SESSION_ID) ?: run {
            log.e { "Couldn't parse reminder notification. Session ID doesn't exist or isn't String." }
            return null
        }

        return Notification.Local.Reminder(
            sessionId = Session.Id(sessionId),
        )
    }

    private fun Intent.parseFeedbackNotification(): Notification.Local.Feedback? {
        val sessionId = this.getStringExtra(Notification.Keys.SESSION_ID) ?: run {
            log.e { "Couldn't parse feedback notification. Session ID doesn't exist or isn't String." }
            return null
        }

        return Notification.Local.Feedback(
            sessionId = Session.Id(sessionId),
        )
    }

    private fun createPendingIntent(id: Int, intentTransform: Intent.() -> Unit = {}): PendingIntent {
        val intent = IdentifiableIntent("$id", context, NotificationPublisher::class.java).apply(intentTransform)
        return PendingIntent.getBroadcast(
            context,
            id,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            },
        )
    }

    private fun nextNotificationId(): Int {
        val notificationId = notificationIdCounter
        notificationIdCounter++
        return notificationId
    }

    private fun saveRegisteredNotificationId(sessionId: Session.Id, notificationId: Int) {
        val currentNotificationIds = (registeredNotifications[sessionId.value] ?: emptyList()).toMutableList()
        currentNotificationIds.add(notificationId)
        registeredNotifications[sessionId.value] = currentNotificationIds
        saveRegisteredNotifications()
    }

    private fun deleteRegisteredNotificationIdSession(sessionId: Session.Id) {
        registeredNotifications.remove(sessionId.value)
        saveRegisteredNotifications()
    }

    private fun saveRegisteredNotifications() {
        settings[NOTIFICATION_ID_MAP_KEY] = json.encodeToString(registeredNotifications)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"

        const val NOTIFICATION_TYPE_SHOW = "SHOW"
        const val NOTIFICATION_TYPE_DISMISS = "DISMISS"

        const val NOTIFICATION_PAYLOAD_ID = "NOTIFICATION_PAYLOAD_ID"
        const val NOTIFICATION_PAYLOAD_TYPE = "NOTIFICATION_PAYLOAD_TYPE"
        const val NOTIFICATION_PAYLOAD_NOTIFICATION = "NOTIFICATION_PAYLOAD_NOTIFICATION"

        const val NOTIFICATION_ID_COUNTER_KEY = "NOTIFICATION_ID_COUNTER"
        const val NOTIFICATION_ID_MAP_KEY = "NOTIFICATION_ID_MAP"

        const val NOTIFICATION_SESSION_ID_EXTRA_KEY = "SESSION_ID"
        const val NOTIFICATION_TYPE_EXTRA_KEY = "TYPE"
        const val NOTIFICATION_TYPE_EXTRA_FEEDBACK = "TYPE_FEEDBACK"
        const val NOTIFICATION_TYPE_EXTRA_REMINDER = "TYPE_REMINDER"

        const val NOTIFICATION_FEEDBACK_REQUEST_CODE = 1
    }
}
