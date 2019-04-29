//
//  AppDelegate.swift
//  iosApp
//
//  Created by Kevin Galligan on 6/21/18.
//  Copyright © 2018 Kevin Galligan. All rights reserved.
//

import UIKit
import lib
import Fabric
import Crashlytics
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        Fabric.with([Crashlytics.self])
        application.statusBarStyle = .lightContent
      
        let serviceRegistry = ServiceRegistry()
        serviceRegistry.doInitLambdas(staticFileLoader: loadAsset, clLogCallback: csLog)
      
        serviceRegistry.doInitServiceRegistry(sqlDriver: FunctionsKt.defaultDriver(),
                                                coroutineDispatcher: UI(),
                                                settings: FunctionsKt.defaultSettings(),
                                                concurrent: MainConcurrent(),
                                                sessionizeApi: SessionizeApiImpl(),
                                                analyticsApi: FunctionsKt.createAnalyticsApiImpl(analyticsCallback: analyticsCallback),
                                                notificationsApi: NotificationsApiImpl(),
                                                timeZone: "-0400")

        let appContext = AppContext()
        
        appContext.doInitAppContext()
        
        appContext.dataLoad()
        
        requestNotificationPermissions()
        
        return true
    }
    
    func requestNotificationPermissions(){
        
        
        
        let isRegisteredForRemoteNotifications = UIApplication.shared.isRegisteredForRemoteNotifications
        if(isRegisteredForRemoteNotifications){
            print("TEst")
        }
        let center = UNUserNotificationCenter.current()
        
        
        center.getNotificationSettings(completionHandler: { (settings) in
            if settings.authorizationStatus == .notDetermined {
                let options: UNAuthorizationOptions = [.alert, .sound];
                center.requestAuthorization(options: options) {
                    (granted, error) in
                    NotificationsKt.setNotificationsEnabled(enabled: granted)
                }
            } else if settings.authorizationStatus == .denied {
                NotificationsKt.setNotificationsEnabled(enabled: false)
            } else if settings.authorizationStatus == .authorized {
                NotificationsKt.setNotificationsEnabled(enabled: true)
            }
        })
        
        
        
        
        
    }

    /*func dispatch(context: KotlinCoroutineContext, block: Kotlinx_coroutines_core_nativeRunnable) -> KotlinUnit {
        DispatchQueue.main.async {
            block.run()
        }
        return KotlinUnit()
    }*/
    
    func csLog(s:String) -> KotlinUnit{
        CLSLogv(s, getVaList([]))
        return KotlinUnit()
    }
    
    func loadAsset(filePrefix:String, fileType:String) -> String?{
        do{
            let bundleFile = Bundle.main.path(forResource: filePrefix, ofType: fileType)
            return try String(contentsOfFile: bundleFile!)
        } catch {
            return nil
        }
    }
    
    func analyticsCallback(name:String, params:[String:Any]) -> KotlinUnit{
        Answers.logCustomEvent(withName: name, customAttributes: params)
        return KotlinUnit()
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
}

