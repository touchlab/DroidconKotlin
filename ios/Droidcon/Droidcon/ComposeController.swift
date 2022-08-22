import SwiftUI
import DroidconKit

struct ComposeController: UIViewControllerRepresentable {
    
    let component: ApplicationComponent
    
    func makeUIViewController(context: Context) -> some UIViewController {
        BackgroundCrashWorkaroundController(component)
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        uiViewController.view.setNeedsLayout()
    }
}
