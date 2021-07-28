import SwiftUI

@main
struct DroidconApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate

    var body: some Scene {
        WindowGroup {
            Text("Hello world!")
        }
    }
}
