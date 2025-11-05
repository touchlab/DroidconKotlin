import SwiftUI
import DroidconKit

struct ComposeController: UIViewControllerRepresentable {
    
    let viewModel: WaitForLoadedContextModel
    
    func makeUIViewController(context: Context) -> some UIViewController {
        getRootController(viewModel: viewModel)
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}
