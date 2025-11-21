package co.touchlab.droidcon.service

import co.touchlab.droidcon.application.service.Notification
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.util.wrapMultiThreadCallback
import co.touchlab.kermit.Logger
import kotlin.time.Instant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitTimeZone
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSError
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

class IOSNotificationService(
    private val log: Logger,
    private val syncService: SyncService,
    private val conferenceConfigProvider: ConferenceConfigProvider,
) : NotificationService {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    private var notificationHandler: DeepLinkNotificationHandler? = null

    override fun setHandler(notificationHandler: DeepLinkNotificationHandler) {
        this.notificationHandler = notificationHandler
    }

    override suspend fun initialize(): Boolean {
        log.d { "Initializing." }

        val notificationSettings = wrapMultiThreadCallback(notificationCenter::getNotificationSettingsWithCompletionHandler)
        if (notificationSettings == null) {
            log.i { "Failed to get current notification authorization." }
            return false
        }
        when (notificationSettings.authorizationStatus) {
            UNAuthorizationStatusNotDetermined -> {
                val requestOptions = UNAuthorizationOptionAlert or UNAuthorizationOptionSound
                val (isAuthorized, error) = wrapMultiThreadCallback<Boolean, NSError?> {
                    notificationCenter.requestAuthorizationWithOptions(requestOptions, it)
                }
                if (error != null) {
                    log.i { "Notifications authorization request failed with '$error'." }
                }
                return isAuthorized
            }

            UNAuthorizationStatusDenied -> {
                log.i { "Notifications not authorized." }
                return false
            }

            UNAuthorizationStatusAuthorized -> {
                log.i { "Notifications authorized." }
                return true
            }

            else -> return false
        }
    }

    override suspend fun schedule(notification: Notification.Local, title: String, body: String, delivery: Instant, dismiss: Instant?) {
        log.v { "Scheduling local notification at ${delivery.toNSDate().description}." }
        val deliveryDate = delivery.toNSDate()
        val allUnits = NSCalendarUnitSecond or
            NSCalendarUnitMinute or
            NSCalendarUnitHour or
            NSCalendarUnitDay or
            NSCalendarUnitMonth or
            NSCalendarUnitYear or
            NSCalendarUnitTimeZone
        val dateComponents = NSCalendar.currentCalendar.components(allUnits, deliveryDate)

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(dateComponents, repeats = false)

        val content = UNMutableNotificationContent()
        content.setTitle(title)
        content.setBody(body)
        content.setSound(UNNotificationSound.defaultSound)
        val (typeValue, sessionId) = when (notification) {
            is Notification.Local.Feedback -> Notification.Values.FEEDBACK_TYPE to notification.sessionId
            is Notification.Local.Reminder -> Notification.Values.REMINDER_TYPE to notification.sessionId
        }
        content.setUserInfo(
            mapOf(
                Notification.Keys.NOTIFICATION_TYPE to typeValue,
                Notification.Keys.SESSION_ID to sessionId.value,
            ),
        )

        val request = UNNotificationRequest.requestWithIdentifier("${sessionId.value}-$typeValue", content, trigger)

        val error = wrapMultiThreadCallback<NSError?> { notificationCenter.addNotificationRequest(request, it) }
        if (error == null) {
            log.v { "Scheduling notification complete." }
        } else {
            log.i { "Scheduling notification error: '$error'" }
        }
    }

    override suspend fun cancel(sessionIds: List<Session.Id>) {
        if (sessionIds.isEmpty()) {
            return
        }
        log.v { "Cancelling scheduled notifications with IDs: [${sessionIds.joinToString { it.value }}]" }
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(sessionIds.map { it.value })
    }

    @Suppress("unused")
    suspend fun didReceiveNotificationResponse(userInfo: Map<Any?, *>) {
        val notification = userInfo.parseNotification() ?: return

        handleNotification(notification)
    }

    @Suppress("unused")
    suspend fun didReceiveRemoteNotification(userInfo: Map<Any?, *>): Boolean {
        val notification = userInfo.parseNotification() ?: return false

        handleNotification(notification)
        return notification is Notification.Remote.RefreshData
    }

    private suspend fun handleNotification(notification: Notification) {
        when (notification) {
            is Notification.DeepLink -> {
                val notificationHandler = notificationHandler
                if (notificationHandler != null) {
                    notificationHandler.handleDeepLinkNotification(notification)
                } else {
                    log.w { "notificationHandler not registered when received $notification" }
                }
            }

            Notification.Remote.RefreshData -> syncService.forceSynchronize(conferenceConfigProvider.getSelectedConference())
        }
    }

    private fun Map<Any?, *>.parseNotification(): Notification? =
        when (val typeValue = this[Notification.Keys.NOTIFICATION_TYPE] as? String) {
            Notification.Values.REMINDER_TYPE -> this.parseReminderNotification()
            Notification.Values.FEEDBACK_TYPE -> this.parseFeedbackNotification()
            Notification.Values.REFRESH_DATA_TYPE -> Notification.Remote.RefreshData
            else -> {
                log.e { "Unknown notification type <$typeValue>, ignoring." }
                null
            }
        }

    private fun Map<Any?, *>.parseReminderNotification(): Notification.Local.Reminder? {
        val sessionId = this[Notification.Keys.SESSION_ID] as? String ?: run {
            log.e { "Couldn't parse reminder notification. Session ID doesn't exist or isn't String." }
            return null
        }

        return Notification.Local.Reminder(
            sessionId = Session.Id(sessionId),
        )
    }

    private fun Map<Any?, *>.parseFeedbackNotification(): Notification.Local.Feedback? {
        val sessionId = this[Notification.Keys.SESSION_ID] as? String ?: run {
            log.e { "Couldn't parse feedback notification. Session ID doesn't exist or isn't String." }
            return null
        }

        return Notification.Local.Feedback(
            sessionId = Session.Id(sessionId),
        )
    }
}
