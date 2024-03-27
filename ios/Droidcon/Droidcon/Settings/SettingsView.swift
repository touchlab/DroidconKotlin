import SwiftUI
import FirebaseAuth
import DroidconKit

struct SettingsView: View {
    @ObservedObject
    private(set) var viewModel: SettingsViewModel
    private let firebaseService = FirebaseService()
    
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

                        if Auth.auth().currentUser != nil {
                            Button("Settings.SignOut"){
                                if let error = firebaseService.signOut() {
                                    errorMessage = error
                                    showingAlert = true
                                }
                            }
                        } else {
                            Button("Settings.SignIn"){
                                firebaseService.signIn(onError: { error in
                                    errorMessage = error
                                    showingAlert = true
                                })
                            }
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
