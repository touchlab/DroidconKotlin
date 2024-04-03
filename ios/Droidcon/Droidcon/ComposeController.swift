import SwiftUI
import DroidconKit

struct ComposeController: UIViewControllerRepresentable {
    
    let viewModel: ApplicationViewModel
    
    func makeUIViewController(context: Context) -> some UIViewController {
        ComposeRootControllerKt.getRootController(viewModel: viewModel, createUIViewController: {
            return UIHostingController(rootView: ChatView())
        })
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}
