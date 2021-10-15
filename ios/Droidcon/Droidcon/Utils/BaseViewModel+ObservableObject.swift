import Combine
import DroidconKit

extension BaseViewModel: Combine.ObservableObject {
    private static var objectWillChangeKey: UInt8 = 0
    public var objectWillChange: ObservableObjectPublisher {
        if let publisher = objc_getAssociatedObject(self, &Self.objectWillChangeKey) as? ObservableObjectPublisher {
            return publisher
        }
        let publisher = ObjectWillChangePublisher()
        objc_setAssociatedObject(self, &Self.objectWillChangeKey, publisher, objc_AssociationPolicy.OBJC_ASSOCIATION_RETAIN)
        changeTracking.addWillChangeObserver {
            publisher.send()
        }
        return publisher
    }
}

extension BaseViewModel: Identifiable {}
