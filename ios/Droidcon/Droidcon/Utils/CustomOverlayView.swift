import SwiftUI

struct CustomOverlayView<Item, Content: View>: UIViewControllerRepresentable {
    @Binding
    var item: Item?
    let settings: CustomOverlaySettings
    let contentFactory: (Item) -> Content

    func makeUIViewController(context: UIViewControllerRepresentableContext<CustomOverlayView>) -> UIViewController {
        UIViewController()
    }

    final class Coordinator {
        var hostingController: UIHostingController<Content>?

        init(_ controller: UIHostingController<Content>? = nil) {
            self.hostingController = controller
        }
    }

    func makeCoordinator() -> Coordinator {
        return Coordinator()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: UIViewControllerRepresentableContext<CustomOverlayView>) {
        if let item = item {
            let hostingController: UIHostingController<Content>
            if let existingHostingController = context.coordinator.hostingController {
                hostingController = existingHostingController
                hostingController.rootView = contentFactory(item)
            } else {
                let newHostingController = UIHostingController(rootView: contentFactory(item))
                newHostingController.modalTransitionStyle = settings.transitionStyle
                newHostingController.modalPresentationStyle = settings.presentationStyle
                newHostingController.view.backgroundColor = settings.backgroundColor
                context.coordinator.hostingController = newHostingController
                hostingController = newHostingController
            }

            if uiViewController.presentedViewController == nil {
                uiViewController.present(hostingController, animated: true)
            }
        } else if let existingHostingController = context.coordinator.hostingController {
            existingHostingController.dismiss(animated: true)
        }
    }
}

public struct CustomOverlaySettings {
    public var transitionStyle: UIModalTransitionStyle
    public var presentationStyle: UIModalPresentationStyle
    public var backgroundColor: UIColor?

    public init(
        transitionStyle: UIModalTransitionStyle = .crossDissolve,
        presentationStyle: UIModalPresentationStyle = .overFullScreen,
        backgroundColor: UIColor? = nil
    ) {
        self.transitionStyle = transitionStyle
        self.presentationStyle = presentationStyle
        self.backgroundColor = backgroundColor
    }
}

extension View {
    public func present<Item, Content: View>(
        item: Binding<Item?>,
        settings: CustomOverlaySettings = CustomOverlaySettings(backgroundColor: UIColor.black.withAlphaComponent(0.5)),
        content: @escaping (Item) -> Content
    ) -> some View {
        self.background(
            CustomOverlayView(item: item, settings: settings, contentFactory: content)
                .frame(width: 0, height: 0, alignment: .topLeading)
        )
    }
}
