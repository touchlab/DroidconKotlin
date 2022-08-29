import SwiftUI
import DroidconKit

class LifecycleManager: SwiftUI.ObservableObject {

    var managedViewModel: BaseViewModel? {
        willSet {
            if let managedViewModel = managedViewModel {
                LoggerKt.d { [root] in "Detaching VM: \(managedViewModel.lifecycle) from \(root)" }
                managedViewModel.lifecycle.removeFromParent()
            }
        }
        didSet {
            if let managedViewModel = managedViewModel {
                LoggerKt.d { [root] in "Attaching VM: \(managedViewModel.lifecycle) to \(root)" }
                root.addChild(child: managedViewModel.lifecycle)
            }
        }
    }

    private let root = LifecycleGraph.Root(owner: "LifecycleManager")
    private let cancelAttach: CancellationToken

    init() {
        LoggerKt.i { [root] in "Initializing LifecycleManager with root: \(root)" }

        cancelAttach = root.attachToMainScope()
    }

    deinit {
        LoggerKt.i { [root] in "Destroying LifecycleManager with root: \(root)" }

        cancelAttach.cancel()
    }
}

struct ManagedLifecycle: ViewModifier {

    private let viewModel: BaseViewModel

    @EnvironmentObject
    private var lifecycleManager: LifecycleManager

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
