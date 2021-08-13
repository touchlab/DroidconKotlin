import UIKit
import Firebase
import DroidconKit

class AppDelegate: NSObject, UIApplicationDelegate {
    // Lazy so it doesn't try to initialize before startKoin() is called
    lazy var log = koin.get(objCClass: Kermit.self, parameter: "AppDelegate") as! Kermit
    lazy var analytics = koin.get(objCProtocol: AnalyticsService.self, qualifier: nil) as! AnalyticsService

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        FirebaseApp.configure()
        startKoin()

        analytics.logEvent(name: AnalyticsServiceCompanion().EVENT_STARTED, params: [:])

        log.v(withMessage: { "App Started" })
        return true
    }
}
