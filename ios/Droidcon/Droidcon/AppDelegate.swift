import UIKit
import Firebase
import FirebaseMessaging
import DroidconKit

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    // Lazy so it doesn't try to initialize before startKoin() is called
    lazy var log: Logger = koin.get(parameters: "AppDelegate")
    lazy var analytics: AnalyticsService = koin.get()
    lazy var appChecker: AppChecker = koin.get()
    lazy var notificationService: IOSNotificationService = koin.get()

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        FirebaseApp.configure()
        AppInitKt.setupKermit()
        
        startKoin()

        try! appChecker.checkTimeZoneHash()

        analytics.logEvent(name: AnalyticsServiceCompanion().EVENT_STARTED, params: [:])
        
        // Initialize conferences in the database
        AppInitKt.initializeConferences()

        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self

        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
          options: authOptions,
          completionHandler: { _, _ in }
        )

        application.registerForRemoteNotifications()

        log.v(message: { "App Started" })
        return true
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        #if DEBUG
        print("APNS Token =", deviceToken.map { String(format: "%02.2hhx", $0) }.joined())
        #endif

        Messaging.messaging().apnsToken = deviceToken
    }

    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any]) async -> UIBackgroundFetchResult {
        log.d { "application(_:didReceiveRemoteNotification:)" }
        Messaging.messaging().appDidReceiveMessage(userInfo)

        do {
            let hasNewData = try await notificationService.didReceiveRemoteNotification(userInfo: userInfo)
         
            return if hasNewData.boolValue {
                .newData
            } else {
                .noData
            }
        } catch {
            return .failed
        }
    }

    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification) async -> UNNotificationPresentationOptions {
        log.d { "userNotificationCenter(_:willPresent:)" }

        let userInfo = notification.request.content.userInfo
        Messaging.messaging().appDidReceiveMessage(userInfo)

        return [.banner, .list, .sound]
    }

    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse) async {
        log.d { "userNotificationCenter(_:didReceive:)" }

        let userInfo = response.notification.request.content.userInfo
        Messaging.messaging().appDidReceiveMessage(userInfo)

        do {
            try await notificationService.didReceiveNotificationResponse(userInfo: userInfo)
        } catch is CancellationError {
            return
        } catch {
            log.e { "notificationService.didReceiveNotificationResponse threw \(error)" }
        }
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        #if DEBUG
        print("FCM Token =", fcmToken ?? "N/A")
        #endif
    }
}
