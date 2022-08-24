import Combine
import DroidconKit

extension BaseViewModel: Combine.ObservableObject {
    private static var objectWillChangePublisherKey: UInt8 = 0
    private static var objectWillChangeTokenKey: UInt8 = 0

    public var objectWillChange: ObservableObjectPublisher {
        if let publisher = objc_getAssociatedObject(self, &Self.objectWillChangePublisherKey) as? ObservableObjectPublisher {
            return publisher
        }
        let publisher = ObjectWillChangePublisher()
        objc_setAssociatedObject(self, &Self.objectWillChangePublisherKey, publisher, objc_AssociationPolicy.OBJC_ASSOCIATION_RETAIN)
        if let previousToken = objc_getAssociatedObject(self, &Self.objectWillChangePublisherKey) as? CancellationToken {
            previousToken.cancel()
        }
        let cancelationToken = changeTracking.addWillChangeObserver {
            publisher.send()
        }
        objc_setAssociatedObject(self, &Self.objectWillChangeTokenKey, cancelationToken, objc_AssociationPolicy.OBJC_ASSOCIATION_RETAIN)
        return publisher
    }
}

extension BaseViewModel: Identifiable {}
