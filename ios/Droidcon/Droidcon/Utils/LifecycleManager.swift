import SwiftUI
import DroidconKit

class LifecycleManager: SwiftUI.ObservableObject {

    var managedViewModel: BaseViewModel? {
        willSet {
            managedViewModel?.lifecycle.detach()
        }
        didSet {
            managedViewModel?.lifecycle.attachToMainScope()
        }
    }

    deinit {
        managedViewModel?.lifecycle.detach()
    }
}

struct ManagedLifecycle: ViewModifier {

    private let viewModel: BaseViewModel

    @StateObject
    private var lifecycleManager = LifecycleManager()

    init(viewModel: BaseViewModel) {
        self.viewModel = viewModel
    }

    func body(content: Content) -> some View {
        content
            .onChange(of: viewModel) { vm in
                lifecycleManager.managedViewModel = vm
            }
            .onAppear {
                lifecycleManager.managedViewModel = viewModel
            }
            .onDisappear {
                lifecycleManager.managedViewModel = nil
            }
    }
}

extension View {
    func attach(viewModel: BaseViewModel) -> some View {
        self.modifier(ManagedLifecycle(viewModel: viewModel))
    }
}
