import Foundation
import DroidconKit

func startKoin() {
    let userDefaults = UserDefaults(suiteName: "DROIDCON_SETTINGS")!

    let koinApplication = DependencyInjectionKt.doInitKoinIos(userDefaults: userDefaults, analyticsService: IOSAnalyticsService())
    _koin = koinApplication.koin
}

private var _koin: Koin_coreKoin? = nil
var koin: Koin_coreKoin {
    return _koin!
}
