//
//  NotificationsApiImpl.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/24/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import lib
import UserNotifications

class NotificationsApiImpl : NSObject, NotificationsApi {

    let reminderTag = "Reminder"
    let feedbackTag = "Feedback"


    func scheduleReminderNotificationsForSessions(sessions: [MySessions]) {
        let notificationModel = NotificationsModel()
        for session in sessions {
            let title = notificationModel.getReminderNotificationTitle(session: session)
            let message = notificationModel.getReminderNotificationMessage(session: session)
            let time = notificationModel.getReminderTimeFromSession(session: session)
            scheduleLocalNotification(title: title, message: message, timeInMS: time, notificationId: Int32(truncatingIfNeeded: session.id.hashValue), notificationTag: reminderTag)
        }
    }

    func scheduleFeedbackNotificationsForSessions(sessions: [MySessions]) {
        let notificationModel = NotificationsModel()
        for session in sessions {
            let title = notificationModel.getFeedbackNotificationTitle()
            let message = notificationModel.getFeedbackNotificationMessage()
            let time = notificationModel.getFeedbackTimeFromSession(session: session)
            scheduleLocalNotification(title: title, message: message, timeInMS: time, notificationId: Int32(truncatingIfNeeded: session.id.hashValue), notificationTag: feedbackTag)
        }
    }

    func cancelReminderNotifications(andDismissals: Bool) {
        cancelNotificationsWithTag(tag: reminderTag)
    }

    func cancelFeedbackNotifications() {
        cancelNotificationsWithTag(tag: feedbackTag)
    }

    private func cancelNotificationsWithTag(tag:String){
        let center = UNUserNotificationCenter.current()
        center.getPendingNotificationRequests(completionHandler: { (requests) in
            for request in requests {
                if request.identifier.starts(with: tag) {
                    center.removePendingNotificationRequests(withIdentifiers: [request.identifier])
                }
            }
        })
    }


    // Needed to approve local notifications
    class LocalNotificationDelegate : NSObject, UNUserNotificationCenterDelegate {
        func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void){
            completionHandler(.alert)
        }
    }

    let notificationDelegate = LocalNotificationDelegate()

    private func scheduleLocalNotification(title: String, message: String, timeInMS: Int64, notificationId: Int32, notificationTag: String) {
        let timeDouble = Double(integerLiteral: timeInMS)
        let date = Date.init(timeIntervalSince1970: timeDouble / 1000.0)
        let dateInfo: DateComponents = Calendar.current.dateComponents([.month,.day,.year,.hour, .minute, .second, .timeZone], from: date)

        let trigger = UNCalendarNotificationTrigger(dateMatching: dateInfo, repeats: false)

        let center = UNUserNotificationCenter.current()
        center.delegate = notificationDelegate

        let content = UNMutableNotificationContent()
        content.title = title
        content.body = message
        content.sound = UNNotificationSound.default()
        
        print("Local Notification Created at \(timeInMS): \(title) - \(message) \n")


        let notifString = String(notificationId)
        let request = UNNotificationRequest(identifier: notifString, content: content, trigger: trigger)
        center.add(request,withCompletionHandler: nil)
    }
    
    private func cancelLocalNotification(notificationId: Int32) {
        let notifString = String(notificationId)
        let identifiers = [notifString]
        
        let center = UNUserNotificationCenter.current()
        center.removePendingNotificationRequests(withIdentifiers: identifiers)        
        print("Cancelling Local Notification")
    }
    
    func initializeNotifications(onSuccess: @escaping (KotlinBoolean) -> Void) {
        let center = UNUserNotificationCenter.current()
        center.getNotificationSettings(completionHandler: { (settings) in
            if settings.authorizationStatus == .notDetermined {
                let options: UNAuthorizationOptions = [.alert, .sound];
                center.requestAuthorization(options: options) {
                    (granted, error) in
                    DispatchQueue.main.async {
                        NotificationsModel().notificationsEnabled = granted
//                        NotificationsModel().setNotificationsEnabled(enabled: granted)
                        _ = onSuccess(KotlinBoolean.init(bool: granted))
                    }
                }
            } else if settings.authorizationStatus == .denied {
                DispatchQueue.main.async {
                    NotificationsModel().notificationsEnabled = false
//                    NotificationsModel().setNotificationsEnabled(enabled: false)
                    _ = onSuccess(false)
                }
            } else if settings.authorizationStatus == .authorized {
                DispatchQueue.main.async {
                    NotificationsModel().notificationsEnabled = true
//                    NotificationsModel().setNotificationsEnabled(enabled: true)
                    _ = onSuccess(true)
                }
            }
        })
    }

    func deinitializeNotifications() {
    }

}
