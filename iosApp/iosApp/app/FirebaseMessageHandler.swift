//
//  FirebaseHandler.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 5/16/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import UserNotifications
import Firebase
import lib

class FirebaseMessageHandler: NSObject, UNUserNotificationCenterDelegate, MessagingDelegate {
    
    override init() {
        super.init()
        
        if #available(iOS 10.0, *) {
            UNUserNotificationCenter.current().delegate = self
            let authOptions: UNAuthorizationOptions = [.alert]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_, _ in })
        } else {
            let settings: UIUserNotificationSettings =
                UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            UIApplication.shared.registerUserNotificationSettings(settings)
        }
        
        UIApplication.shared.registerForRemoteNotifications()
        Messaging.messaging().delegate = self
    }
    
    func messaging(_ messaging: Messaging, didReceive remoteMessage: MessagingRemoteMessage) {
        print("From: \(remoteMessage.messageID)")
        print("Message data payload: \(remoteMessage.appData)")
        NetworkRepo().dataCalls()
    }
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
    }
    
    
    static func initMessaging(){
        Messaging.messaging().shouldEstablishDirectChannel = true
        Messaging.messaging().useMessagingDelegateForDirectChannel = true
        
        Messaging.messaging().subscribe(toTopic: "all")
    }
}
