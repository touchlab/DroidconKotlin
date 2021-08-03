package co.touchlab.droidcon.application.service.impl

import co.touchlab.droidcon.application.repository.SettingsRepository
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.domain.repository.SessionRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
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
): NotificationSchedulingService {
    private companion object {
        // MARK: Settings keys
        private const val SCHEDULED_NOTIFICATIONS_KEY = "SCHEDULED_NOTIFICATIONS_KEY"

        // MARK: Delivery offsets (in minutes)
        private const val REMINDER_DELIVERY_OFFSET: Long = -10
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
        if (!isNotificationsAuthorized) { return }

        coroutineScope {
            launch {
                sessionRepository.observeAllAttending()
                    .combine(
                        settingsRepository.settings.map { it.isRemindersEnabled },
                        transform = { agenda, isRemindersEnabled -> agenda to isRemindersEnabled }
                    )
                    .collect { (agenda, isRemindersEnabled) ->
                        println("agenda: $agenda, reminders: $isRemindersEnabled")
                        if (isRemindersEnabled) {
                            val scheduledNotificationIds = scheduledNotifications

                            // Cancel sessions that the user isn't attending anymore.
                            notificationService.cancel(
                                scheduledNotificationIds.minus(agenda.map { it.id })
                            )

                            // Schedule new upcoming sessions.
                            val newSessions = agenda.filterNot { scheduledNotificationIds.contains(it.id) }
                            for (session in newSessions) {
                                val roomName = session.room?.let { roomRepository.get(it).name }
                                notificationService.schedule(
                                    sessionId = session.id,
                                    title = "Upcoming event${roomName?.let { " in $it" } ?: ""}",
                                    body = "${session.title} is starting soon.",
                                    delivery = session.startsAt.plus(REMINDER_DELIVERY_OFFSET, DateTimeUnit.MINUTE),
                                )
                            }

                            scheduledNotifications = scheduledNotificationIds.plus(newSessions.map { it.id })
                        } else {
                            notificationService.cancel(scheduledNotifications)
                            scheduledNotifications = emptyList()
                        }
                    }
            }
        }

        println("Scheduling done!")
    }
}