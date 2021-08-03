package co.touchlab.droidcon.service

import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.util.wrapMultiThreadCallback
import co.touchlab.kermit.Kermit
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import kotlinx.datetime.Instant
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
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionAlert
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

@OptIn(
    ExperimentalUnsignedTypes::class,
    ExperimentalSettingsApi::class,
)
class IOSNotificationService(
    private val log: Kermit,
): NotificationService {
    private val notificationDelegate: UNUserNotificationCenterDelegateProtocol by lazy {
        NotificationDelegate()
    }

    private val notificationCenter: UNUserNotificationCenter by lazy {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.delegate = notificationDelegate
        center
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
                val (isAuthorized, error) = wrapMultiThreadCallback<Boolean, NSError?> { notificationCenter.requestAuthorizationWithOptions(requestOptions, it) }
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

    override suspend fun schedule(sessionId: Session.Id, title: String, body: String, delivery: Instant) {
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

        val request = UNNotificationRequest.requestWithIdentifier(sessionId.value, content, trigger)

        val error = wrapMultiThreadCallback<NSError?> { notificationCenter.addNotificationRequest(request, it) }
        if (error == null) {
            log.v { "Scheduling notification complete." }
        } else {
            log.i { "Scheduling notification error: '$error'" }
        }
    }

    override suspend fun cancel(sessionIds: List<Session.Id>) {
        if (sessionIds.isEmpty()) { return }
        log.v { "Cancelling scheduled notifications with IDs: [${sessionIds.joinToString { it.value }}]" }
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(sessionIds.map { it.value })
    }

    // Delegate necessary to show notification.
    private class NotificationDelegate: NSObject(), UNUserNotificationCenterDelegateProtocol {
        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            willPresentNotification: UNNotification,
            withCompletionHandler: (UNNotificationPresentationOptions) -> Unit,
        ) {
            withCompletionHandler(UNNotificationPresentationOptionAlert)
        }
    }
}
