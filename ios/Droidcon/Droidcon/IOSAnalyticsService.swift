import Foundation
import Firebase
import DroidconKit

final class IOSAnalyticsService: AnalyticsService {
    func logEvent(name: String, params: [String: Any]) {
        Analytics.logEvent(name, parameters: params)
    }
}
