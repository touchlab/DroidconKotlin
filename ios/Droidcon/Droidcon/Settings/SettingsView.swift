import SwiftUI
import DroidconKit

struct SettingsView: View {
    private var component: SettingsComponent
    
    @ObservedObject
    private var observableModel: ObservableValue<SettingsComponent.Model>
    
    private var viewModel: SettingsComponent.Model { observableModel.value }
    
    init(_ component: SettingsComponent) {
        self.component = component
        self.observableModel = ObservableValue(component.model)
    }

    var body: some View {
        NavigationView {
            ZStack {
                ScrollView {
                    VStack(alignment: .leading, spacing: 0) {
                        Toggle(isOn: Binding(get: { viewModel.isFeedbackEnabled }, set: component.setFeedbackEnabled)) {
                            Label("Settings.Feedback", systemImage: "ellipsis.bubble")
                        }
                        .padding(.vertical, 8)
                        .padding(.horizontal)

                        Divider().padding(.horizontal)

                        Toggle(isOn: Binding(get: { viewModel.isRemindersEnabled }, set: component.setRemindersEnabled)) {
                            Label("Settings.Reminders", systemImage: "calendar")
                        }
                        .padding(.vertical, 8)
                        .padding(.horizontal)
                        
                        Divider().padding(.horizontal)

                        Toggle(isOn: Binding(get: { viewModel.useComposeForIos }, set: component.setUseComposeForIos)) {
                            Label("Settings.Compose", systemImage: "doc.text.image")
                        }
                        .padding(.vertical, 8)
                        .padding(.horizontal)

                        Divider().padding(.horizontal)

                        AboutView(component.about)
                    }
                }
            }
            .navigationTitle("Settings.Title")
            .navigationBarTitleDisplayMode(.inline)
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}

struct SettingsView_Previews: PreviewProvider {
    static var previews: some View {
//        SettingsView()
        EmptyView()
    }
}
