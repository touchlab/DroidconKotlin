package co.touchlab.droidcon.application.service.impl

import co.touchlab.droidcon.application.composite.Settings
import co.touchlab.droidcon.application.repository.SettingsRepository
import co.touchlab.droidcon.application.service.Notification
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.service.DateTimeService
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.set
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DefaultNotificationSchedulingService(
    private val sessionRepository: SessionRepository,
    private val roomRepository: RoomRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationService: NotificationService,
    private val dateTimeService: DateTimeService,
    private val settings: ObservableSettings,
    private val json: Json,
    private val localizedStringFactory: NotificationSchedulingService.LocalizedStringFactory,
) : NotificationSchedulingService {
    private companion object {
        // MARK: Settings keys
        private const val SCHEDULED_NOTIFICATIONS_KEY = "SCHEDULED_NOTIFICATIONS_KEY"
    }

    private var scheduledNotifications: List<Session.Id>
        get() = settings.getStringOrNull(SCHEDULED_NOTIFICATIONS_KEY)?.let { serializedList ->
            json.decodeFromString<List<String>>(serializedList).map { Session.Id(it) }
        } ?: emptyList()
        set(value) {
            settings[SCHEDULED_NOTIFICATIONS_KEY] = json.encodeToString(value.map { it.value })
        }

    override suspend fun runScheduling() {
        notificationService.initialize()
        coroutineScope {
            launch {
                scheduleNotifications(
                    sessionRepository.observeAllAttending(),
                    settingsRepository.settings,
                )
            }
        }
    }

    override suspend fun rescheduleAll() {
        scheduledNotifications = emptyList()
        scheduleNotifications(
            sessionRepository.observeAllAttending().take(1),
            settingsRepository.settings.take(1),
        )
    }

    private suspend fun scheduleNotifications(sessionFlow: Flow<List<Session>>, settingsFlow: Flow<Settings>) {
        sessionFlow
            .combine(
                settingsFlow,
                transform = { agenda, settings -> Triple(agenda, settings.isRemindersEnabled, settings.isFeedbackEnabled) },
            )
            .distinctUntilChanged()
            .collect { (agenda, isRemindersEnabled, isFeedbackEnabled) ->
                if (isRemindersEnabled || isFeedbackEnabled) {
                    val scheduledSessionIds = scheduledNotifications

                    // Cancel sessions that the user isn't attending anymore.
                    val oldSessionIds = scheduledSessionIds.filterNot { sessionId ->
                        agenda.map { it.id.value }.contains(sessionId.value)
                    }
                    scheduledNotifications = scheduledNotifications.filterNot { oldSessionIds.contains(it) }
                    notificationService.cancel(oldSessionIds)

                    // Schedule new upcoming sessions.
                    val newSessions = agenda.filterNot { scheduledSessionIds.contains(it.id) }
                    for (session in newSessions) {
                        if (isRemindersEnabled) {
                            val roomName = session.room?.let { roomRepository.get(it).name }
                            val reminderDelivery =
                                session.startsAt.plus(NotificationSchedulingService.REMINDER_DELIVERY_START_OFFSET, DateTimeUnit.MINUTE)
                            if (session.endsAt >= dateTimeService.now()) {
                                notificationService.schedule(
                                    notification = Notification.Local.Reminder(
                                        sessionId = session.id,
                                    ),
                                    title = localizedStringFactory.reminderTitle(roomName),
                                    body = localizedStringFactory.reminderBody(session.title),
                                    delivery = reminderDelivery,
                                    dismiss = reminderDelivery.plus(
                                        NotificationSchedulingService.REMINDER_DISMISS_OFFSET,
                                        DateTimeUnit.MINUTE,
                                    ),
                                )
                            }
                        }

                        if (isFeedbackEnabled) {
                            val feedbackDelivery =
                                session.endsAt.plus(NotificationSchedulingService.FEEDBACK_DISMISS_END_OFFSET, DateTimeUnit.MINUTE)
                            if (feedbackDelivery.plus(24, DateTimeUnit.HOUR) >= dateTimeService.now() && session.feedback == null) {
                                notificationService.schedule(
                                    notification = Notification.Local.Feedback(
                                        sessionId = session.id,
                                    ),
                                    title = localizedStringFactory.feedbackTitle(),
                                    body = localizedStringFactory.feedbackBody(),
                                    delivery = feedbackDelivery,
                                    dismiss = null,
                                )
                            }
                        }
                    }

                    scheduledNotifications += newSessions.map { it.id }
                } else {
                    notificationService.cancel(scheduledNotifications)
                    scheduledNotifications = emptyList()
                }
            }
    }
}
