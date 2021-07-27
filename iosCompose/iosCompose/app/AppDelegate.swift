//
//  AppDelegate.swift
//  iosApp
//
//  Created by Kevin Galligan on 6/21/18.
//  Copyright © 2018 Kevin Galligan. All rights reserved.
//

import UIKit
import shared
import UserNotifications
import Firebase

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    lazy var nai = NotificationsApiImpl()
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let path = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") ?? ""
        let fileExists = FileManager.default.fileExists(atPath: path)
        if(fileExists){
            FirebaseApp.configure()
//            FunctionsKt.crashInit(handler: CrashlyticsCrashHandler())
        }else{
            print("Firebase plist not found: Firebased Not Enabled")
        }
        
        application.statusBarStyle = .lightContent

        let timeZone = Bundle.main.object(forInfoDictionaryKey: "TimeZone") as! String
        KoinIOSKt.injectModule(
            analyticsApi: FunctionsKt.createAnalyticsApiImpl(analyticsCallback: analyticsCallback),
            timeZone: timeZone,
            notificationsApi: nai,
            staticFileLoader: loadAsset,
            ciLogCallback: csLog,
            softExceptionCallback: softExceptionCallback
        )
        
//        serviceRegistry.doInitLambdas(staticFileLoader: loadAsset, clLogCallback: csLog, softExceptionCallback: softExceptionCallback)
//
//
//        serviceRegistry.doInitServiceRegistry(sqlDriver: FunctionsKt.defaultDriver(),
//                                                settings: FunctionsKt.defaultSettings(),
//                                                sessionizeApi: SessionizeApiImpl(),
//                                                analyticsApi: FunctionsKt.createAnalyticsApiImpl(analyticsCallback: analyticsCallback),
//                                                notificationsApi: NotificationsApiImpl(),
//                                                timeZone: timeZone)
//
//
//        AppContext().doInitAppContext(networkRepo: NetworkRepo(), fileRepo: FileRepo(), serviceRegistry: ServiceRegistry(), dbHelper: SessionizeDbHelper(), notificationsModel: NotificationsModel())
//
//        NetworkRepo().sendFeedback()
        
        if(fileExists){
            FirebaseMessageHandler.initMessaging()
        }
        return true
    }

    func softExceptionCallback(e:KotlinThrowable, message:String) {
    }
    
    func csLog(s:String) {
        
//        CLSLogv(s, getVaList([]))
    }

    func loadAsset(filePrefix:String, fileType:String) -> String?{
        do{
            let bundleFile = Bundle.main.path(forResource: filePrefix, ofType: fileType)
            return try String(contentsOfFile: bundleFile!)
        } catch {
            return nil
        }
    }

    func analyticsCallback(name:String, params:[String:Any]) {
        Analytics.logEvent(name, parameters: params)
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
        nai.deinitializeNotifications()
    }
}

//class CrashlyticsCrashHandler: CrashkiosCrashHandler {
//    override func crashParts(
//        addresses: [KotlinLong],
//        exceptionType: String,
//        message: String) {
//        let clsStackTrace = addresses.map {
//            CLSStackFrame(address: UInt(truncating: $0))
//        }
//
//        Crashlytics.sharedInstance().recordCustomExceptionName(
//            exceptionType,
//            reason: message,
//            frameArray: clsStackTrace
//        )
//    }
//}
