import UIKit
import DroidconKit

class AppDelegate: NSObject, UIApplicationDelegate {
    // Lazy so it doesn't try to initialize before startKoin() is called
    lazy var log = koin.get(objCClass: Kermit.self, parameter: "AppDelegate") as! Kermit

    func application(_ application: UIApplication, didFinishLaunchingWithOptions
        launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

        startKoin()

        log.v(withMessage: { "App Started" })
        return true
    }
}
