import Foundation

class SettingsBundleHelper {
    
    private static let UseComposeKey = "use_compose_preference"
    
    class func initialize() {
        UserDefaults.standard.register(defaults: [UseComposeKey : true])
    }
    
    class func getUseComposeValue() -> Bool {
        return UserDefaults.standard.bool(forKey: UseComposeKey)
    }
    
    class func setUseComposeValue(newValue: Bool) {
        UserDefaults.standard.set(newValue, forKey: UseComposeKey)
    }
}
