import SwiftUI
import DroidconKit

struct SwitchingRootView: View {
    
    private let component: ApplicationComponent
    
    @ObservedObject
    private var observableModel: ObservableValue<ApplicationComponent.Model>
    
    private var model: ApplicationComponent.Model { observableModel.value }
    
    private let userDefaultsPublisher = NotificationCenter.default.publisher(for: UserDefaults.didChangeNotification)
    
    init(_ component: ApplicationComponent) {
        self.component = component
        self.observableModel = ObservableValue(component.model)
    }
    
    var body: some View {
        VStack {
            if model.useComposeForIos {
                // Uncomment after verifying Compose for iOS with Decompose
//                ZStack {
//                    Color("NavBar_Background")
//                        .ignoresSafeArea()
//
//                    ComposeController(component: component)
//                }

                // Remove after verifyin Compose for iOS with Decompose
                MainView(component)
            } else {
                MainView(component)
            }
        }
    }
}

struct SwitchingRootView_Previews: PreviewProvider {
    static var previews: some View {
        EmptyView()
    }
}
