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

class FirebaseHandler: NSObject, UNUserNotificationCenterDelegate, MessagingDelegate {

    var token:String?
    
    override init() {
        super.init()
        
        if #available(iOS 10.0, *) {
            // For iOS 10 display notification (sent via APNS)
            UNUserNotificationCenter.current().delegate = self
            
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
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
    
    func firebaseRequestToken(){
        InstanceID.instanceID().instanceID { (result, error) in
            if let error = error {
                print("Error fetching remote instance ID: \(error)")
            } else if let result = result {
                print("Remote instance ID token: \(result.token)")
                self.token = result.token
            }
        }
    }
    
    func messaging(_ messaging: Messaging, didReceive remoteMessage: MessagingRemoteMessage) {
        NetworkRepo().dataCalls()
    }
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
        print("Firebase registration token: \(fcmToken)")
        
        let dataDict:[String: String] = ["token": fcmToken]
        NotificationCenter.default.post(name: Notification.Name("FCMToken"), object: nil, userInfo: dataDict)
        // TODO: If necessary send token to application server.
        // Note: This callback is fired at each app startup and whenever a new token is generated.
        token = fcmToken
    }
}
