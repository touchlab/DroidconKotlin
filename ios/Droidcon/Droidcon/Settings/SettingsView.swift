import SwiftUI
import DroidconKit

struct SettingsView: View {
    @ObservedObject
    private(set) var viewModel: SettingsViewModel

    var body: some View {
        NavigationView {
            ZStack {
                SwitchingNavigationLink(
                    selection: $viewModel.presentedAbout,
                    content: { AboutView(viewModel: $0) }
                )

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

                        Button(action: viewModel.aboutTapped) {
                            Label("Settings.About", systemImage: "info.circle")
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .padding(.vertical, 8)
                                .padding(.horizontal)
                        }
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
