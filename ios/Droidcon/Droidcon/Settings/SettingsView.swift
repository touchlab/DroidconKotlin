import SwiftUI
import DroidconKit

struct SettingsView: View {
    @ObservedObject
    private(set) var viewModel: SettingsViewModel

    var body: some View {
        NavigationView {
            ZStack {
                GeometryReader { geometry in
                    ScrollView {
                        VStack(alignment: .leading, spacing: 0) {
                            NavigationLink(
                                destination: ComposeController(viewModel: viewModel)
                                    .frame(height: geometry.size.height)
                            ) {
                                Text("Try out in Compose for iOS!")
                                    .padding()
                                    .frame(maxWidth: .infinity)
                            }

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

struct ComposeController: UIViewControllerRepresentable {
    
    let viewModel: SettingsViewModel
    
    func makeUIViewController(context: Context) -> some UIViewController {
        SettingsTestViewKt.getRootController(viewModel: viewModel)
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        uiViewController.view.setNeedsLayout()
    }
}
