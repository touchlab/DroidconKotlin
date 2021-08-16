package co.touchlab.droidcon.application.service.impl

import co.touchlab.droidcon.application.repository.SettingsRepository
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.domain.repository.SessionRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.set
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSettingsApi::class)
class DefaultNotificationSchedulingService(
    private val sessionRepository: SessionRepository,
    private val roomRepository: RoomRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationService: NotificationService,
    private val settings: ObservableSettings,
    private val json: Json,
    private val localizedStringFactory: NotificationSchedulingService.LocalizedStringFactory,
): NotificationSchedulingService {
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
        val isNotificationsAuthorized = notificationService.initialize()

        coroutineScope {
            launch {
                scheduleNotifications(
                    sessionRepository.observeAllAttending()
                )
            }
        }
    }

    override suspend fun rescheduleAll() {
        scheduledNotifications = emptyList()
        scheduleNotifications(sessionRepository.observeAllAttending().take(1))
    }

    private suspend fun scheduleNotifications(flow: Flow<List<Session>>) {
        flow
            .combine(
                settingsRepository.settings,
                transform = { agenda, settings -> Triple(agenda, settings.isRemindersEnabled, settings.isFeedbackEnabled) }
            )
            .collect { (agenda, isRemindersEnabled, isFeedbackEnabled) ->
                if (isRemindersEnabled || isFeedbackEnabled) {
                    val scheduledNotificationIds = scheduledNotifications

                    // Cancel sessions that the user isn't attending anymore.
                    notificationService.cancel(
                        scheduledNotificationIds.filterNot { sessionId ->
                            agenda.map { it.id.value }.contains(sessionId.value)
                        }
                    )

                    // Schedule new upcoming sessions.
                    val newSessions = agenda.filterNot { scheduledNotificationIds.contains(it.id) }
                    for (session in newSessions) {
                        if (isRemindersEnabled) {
                            val roomName = session.room?.let { roomRepository.get(it).name }
                            val reminderDelivery = session.startsAt.plus(NotificationSchedulingService.REMINDER_DELIVERY_START_OFFSET, DateTimeUnit.MINUTE)
                            notificationService.schedule(
                                sessionId = session.id,
                                title = localizedStringFactory.reminderTitle(roomName),
                                body = localizedStringFactory.reminderBody(session.title),
                                delivery = reminderDelivery,
                                dismiss = reminderDelivery.plus(NotificationSchedulingService.REMINDER_DISMISS_OFFSET, DateTimeUnit.MINUTE),
                            )
                        }

                        if (isFeedbackEnabled) {
                            val feedbackDelivery = session.endsAt.plus(NotificationSchedulingService.FEEDBACK_DISMISS_END_OFFSET, DateTimeUnit.MINUTE)
                            notificationService.schedule(
                                sessionId = session.id,
                                title = localizedStringFactory.feedbackTitle(),
                                body = localizedStringFactory.feedbackBody(),
                                delivery = feedbackDelivery,
                                dismiss = null,
                            )
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