import SwiftUI
import DroidconKit

struct SwitchingRootView: View {
    
    @ObservedObject
    var viewModel: ApplicationViewModel
    
    private let userDefaultsPublisher = NotificationCenter.default.publisher(for: UserDefaults.didChangeNotification)
    
    var body: some View {
        Group {
            if viewModel.useCompose {
                ZStack {
                    Color("NavBar_Background")
                        .ignoresSafeArea()
                    
                    ComposeController(viewModel: viewModel)
                }
            } else {
                MainView(viewModel: viewModel)
            }
        }
        .attach(viewModel: viewModel)
        .onAppear(perform: viewModel.onAppear)
        .onReceive(userDefaultsPublisher) { _ in
            let x = SettingsBundleHelper.getUseComposeValue()
            viewModel.useCompose = x
            print("Initial compose: \(x)")
        }
        .onChange(of: viewModel.useCompose) { newValue in
            SettingsBundleHelper.setUseComposeValue(newValue: newValue)
        }
    }
}

struct SwitchingRootView_Previews: PreviewProvider {
    static var previews: some View {
        EmptyView()
    }
}
