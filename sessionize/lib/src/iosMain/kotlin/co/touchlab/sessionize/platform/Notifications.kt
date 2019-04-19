package co.touchlab.sessionize.platform

import co.touchlab.sessionize.AppContext
import co.touchlab.stately.freeze
import co.touchlab.stately.isFrozen
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnit
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitTimeZone
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationOptions
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationPresentationOptionAlert
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

private val localNotificationDelegate = LocalNotificationDelegate()

@ExperimentalUnsignedTypes
actual fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int) {

    val date = NSDate.dateWithTimeIntervalSince1970(timeInMS / 1000.0)
    var dateFlags: NSCalendarUnit = NSCalendarUnitMonth.or(NSCalendarUnitDay).or(NSCalendarUnitYear)
    var timeFlags: NSCalendarUnit = NSCalendarUnitHour.or(NSCalendarUnitMinute).or(NSCalendarUnitSecond).or(NSCalendarUnitTimeZone)
    val dateInfo = NSCalendar.currentCalendar.components(dateFlags.or(timeFlags),date)

    val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(dateInfo, false)

    val center = UNUserNotificationCenter.currentNotificationCenter()
    center.delegate = localNotificationDelegate

    val content = UNMutableNotificationContent()
    content.setTitle(title)
    content.setBody(message)
    content.setSound(UNNotificationSound.defaultSound)

    val request = UNNotificationRequest.requestWithIdentifier(notificationId.toString(), content, trigger)
    center.addNotificationRequest(request,null)
}

actual fun cancelLocalNotification(notificationId: Int){
    val center = UNUserNotificationCenter.currentNotificationCenter()
    val identifiers:Array<String> = arrayOf(notificationId.toString())
    val listIds = identifiers.asList()
    listIds.freeze()
    if (listIds.isFrozen()) {
        center.removePendingNotificationRequestsWithIdentifiers(listIds)
        center.removeDeliveredNotificationsWithIdentifiers(listIds)
    }
}

actual fun initializeNotifications(){
    val center = UNUserNotificationCenter.currentNotificationCenter()
    val options: UNAuthorizationOptions = [UNAuthorizationOptionAlert, UNAuthorizationOptionSound]
    center.requestAuthorizationWithOptions(options) { enabled, error ->
        setNotificationsEnabled(enabled)
    }
}

actual fun deinitializeNotifications() {
}

// Needed to approve local notifications
class LocalNotificationDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {

    override fun userNotificationCenter(center: platform.UserNotifications.UNUserNotificationCenter,
                                        willPresentNotification: platform.UserNotifications.UNNotification,
                                        withCompletionHandler: (platform.UserNotifications.UNNotificationPresentationOptions) -> kotlin.Unit) {
        withCompletionHandler(UNNotificationPresentationOptionAlert)
    }
}