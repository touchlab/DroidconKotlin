import UIKit
import FirebaseAuth
import GoogleSignIn
import Firebase
import DroidconKit

class AppDelegate: NSObject, UIApplicationDelegate {
    // Lazy so it doesn't try to initialize before startKoin() is called
    lazy var log = koin.get(objCClass: Logger.self, parameter: "AppDelegate") as! Logger
    lazy var analytics = koin.get(objCProtocol: AnalyticsService.self, qualifier: nil) as! AnalyticsService
    lazy var appChecker = koin.get(objCClass: AppChecker.self) as! AppChecker

    var firebaseAuthListener:AuthStateDidChangeListenerHandle?
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        FirebaseApp.configure()
        AppInitKt.setupKermit()
        
        startKoin()
        
        try! appChecker.checkTimeZoneHash()

        analytics.logEvent(name: AnalyticsServiceCompanion().EVENT_STARTED, params: [:])

    
        firebaseAuthListener = Auth.auth().addStateDidChangeListener() { auth, user in
            // TODO
            if let user {
                _ = UserContext(
                    isAuthenticated: false,
                    userData: UserData(id: user.uid, name: user.displayName, email: user.email, pictureUrl: user.photoURL?.absoluteString)
                )
            } else {
                _ = UserContext(isAuthenticated: false, userData: nil)
            }
        }
        
        log.v(message: { "App Started" })
        return true
    }
    
    func application(_ app: UIApplication,
                     open url: URL,
                     options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> Bool {
        return GIDSignIn.sharedInstance.handle(url)
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        if let firebaseAuthListener {
            Auth.auth().removeStateDidChangeListener(firebaseAuthListener)
        }
    }
    
}
