import Foundation
import DroidconKit

func startKoin() {
    let userDefaults = UserDefaults(suiteName: "DROIDCON2023_SETTINGS")!

    let koinApplication = DependencyInjectionKt.doInitKoinIos(
        userDefaults: userDefaults,
        analyticsService: IOSAnalyticsService(),
        googleSignInService: IOSGoogleSignInService()
    )
    _koin = koinApplication.koin
}

private var _koin: Koin_coreKoin? = nil
var koin: Koin_coreKoin {
    return _koin!
}
