import Foundation
import DroidconKit

func startKoin() {
    let userDefaults = UserDefaults(suiteName: "DROIDCON2023_SETTINGS")!

    let koinApplication = DependencyInjectionKt.doInitKoinIos(userDefaults: userDefaults, analyticsService: IOSAnalyticsService())
    _koin = koinApplication.koin
}

private var _koin: Koin_coreKoin? = nil
var koin: Koin_coreKoin {
    return _koin!
}

extension Koin_coreKoin {
    func get<T: AnyObject>(_ type: T.Type = T.self, qualifier: Koin_coreQualifier? = nil, parameters: Any...) -> T {
        return getAny(
            objCObject: type,
            qualifier: qualifier,
            parameters: parameters.isEmpty ? nil : {
                Koin_coreParametersHolder(_values: NSMutableArray(array: parameters), useIndexedValues: KotlinBoolean(bool: false))
            }
        ) as! T
    }
}
