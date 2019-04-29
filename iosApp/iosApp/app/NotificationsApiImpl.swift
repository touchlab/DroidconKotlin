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
    
    // Needed to approve local notifications
    class LocalNotificationDelegate : NSObject, UNUserNotificationCenterDelegate {
        func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void){
            completionHandler(.alert)
        }
    }
    
    let notificationDelegate = LocalNotificationDelegate()
    
    func createLocalNotification(title: String, message: String, timeInMS: Int64, notificationId: Int32) {
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
        
        let notifString = String(notificationId)
        let request = UNNotificationRequest(identifier: notifString, content: content, trigger: trigger)
        center.add(request,withCompletionHandler: nil)
        
    }
    
    func cancelLocalNotification(notificationId: Int32) {
        let center = UNUserNotificationCenter.current()
        let identifiers = [String(notificationId)]
        center.removePendingNotificationRequests(withIdentifiers: identifiers)
        center.removeDeliveredNotifications(withIdentifiers: identifiers)
    }
    
    func initializeNotifications() {
    }
    
    func deinitializeNotifications() {
    }
    
}
