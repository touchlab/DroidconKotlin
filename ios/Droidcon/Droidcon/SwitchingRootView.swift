import SwiftUI
import DroidconKit

struct SwitchingRootView: View {
    
    @ObservedObject
    var viewModel: ApplicationViewModel
    
    private let userDefaultsPublisher = NotificationCenter.default.publisher(for: UserDefaults.didChangeNotification)
    
    private let appActivePublisher = NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)
    
    var body: some View {
        Group {
            if viewModel.useCompose {
                ComposeController(viewModel: viewModel)
                .ignoresSafeArea()
            } else {
                MainView(viewModel: viewModel)
            }
        }
        .attach(viewModel: viewModel)
        .onAppear(perform: viewModel.onAppear)
        .onReceive(userDefaultsPublisher) { _ in
            viewModel.useCompose = SettingsBundleHelper.getUseComposeValue()
        }
        .onChange(of: viewModel.useCompose) { newValue in
            SettingsBundleHelper.setUseComposeValue(newValue: newValue)
        }
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
