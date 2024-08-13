import SwiftUI
import DroidconKit

struct SwitchingRootView: View {
    
    @ObservedObject
    var viewModel: ApplicationViewModel
    
    private let userDefaultsPublisher = NotificationCenter.default.publisher(for: UserDefaults.didChangeNotification)
    
    private let appActivePublisher = NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)
    
    var body: some View {
        Group {
            ComposeController(viewModel: viewModel).ignoresSafeArea()
        }
        .attach(viewModel: viewModel)
        .onAppear(perform: viewModel.onAppear)
        .onReceive(appActivePublisher) { _ in
            viewModel.onAppear()
        }
        
    }
}

struct SwitchingRootView_Previews: PreviewProvider {
    static var previews: some View {
        EmptyView()
    }
}
