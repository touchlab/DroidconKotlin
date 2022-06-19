import SwiftUI
import DroidconKit

struct SettingsView: View {
    @ObservedObject
    private(set) var viewModel: SettingsViewModel

    var body: some View {
        NavigationView {
            ZStack {
                ScrollView {
                    VStack(alignment: .leading, spacing: 0) {
                        Toggle(isOn: $viewModel.isFeedbackEnabled) {
                            Label("Settings.Feedback", systemImage: "ellipsis.bubble")
                        }
                        .padding(.vertical, 8)
                        .padding(.horizontal)

                        Divider().padding(.horizontal)

                        Toggle(isOn: $viewModel.isRemindersEnabled) {
                            Label("Settings.Reminders", systemImage: "calendar")
                        }
                        .padding(.vertical, 8)
                        .padding(.horizontal)

                        Divider().padding(.horizontal)

                        AboutView(viewModel: viewModel.about)
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
