import Foundation
import DroidconKit

func startKoin() {

    let userDefaults = UserDefaults(suiteName: "DROIDCON_SETTINGS")!
    let iosAppInfo = IosAppInfo()

    let koinApplication = KoinIOSKt.doInitKoinIos(userDefaults: userDefaults, appInfo: iosAppInfo)
    _koin = koinApplication.koin
}

private var _koin: Koin? = nil
var koin: Koin {
    return _koin!
}

class IosAppInfo : AppInfo {
    let appId: String = Bundle.main.bundleIdentifier!
}
