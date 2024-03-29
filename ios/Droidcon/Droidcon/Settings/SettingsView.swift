import SwiftUI
import FirebaseAuth
import DroidconKit

struct SettingsView: View {
    @ObservedObject
    private(set) var viewModel: SettingsViewModel
    
    @State var errorMessage: String = ""
    @State var showingAlert: Bool = false
    
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

                        Toggle(isOn: $viewModel.useCompose) {
                            Label("Settings.Compose", systemImage: "doc.text.image")
                        }
                        .padding(.vertical, 8)
                        .padding(.horizontal)

                        Divider().padding(.horizontal)

                        if viewModel.isAuthenticated {
                            Button("Settings.SignOut"){
                                if viewModel.signOut() {
                                    errorMessage = ""
                                    showingAlert = false
                                } else {
                                    errorMessage = "Failed to Sign Out"
                                    showingAlert = true
                                }
                            }
                            .buttonStyle(FilledButtonStyle())
                        } else {
                            Button(action: { 
                                if viewModel.signIn() {
                                    errorMessage = ""
                                    showingAlert = false
                                } else {
                                    errorMessage = "Failed To Sign In"
                                    showingAlert = true
                                }
                            }, label: {
                                Image("continue_with_google_rd")
                            })
                        }
                        
                        AboutView(viewModel: viewModel.about)
                    }
                }
            }
            .navigationTitle("Settings.Title")
            .navigationBarTitleDisplayMode(.inline)
            .alert(isPresented: $showingAlert, content: {
                Alert(
                    title: Text("Error Occurred"),
                    message: Text(errorMessage),
                    dismissButton: .default(Text("Got it!")){
                        showingAlert = false
                    }
                )
            })
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
