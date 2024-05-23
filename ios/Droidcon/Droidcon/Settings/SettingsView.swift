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
                        HStack{
                            Image(systemName: "person.fill")
                                .padding(EdgeInsets(top: 20, leading: 16, bottom: 20, trailing: 0))
                            VStack(alignment: .leading) {
                                Text("Settings.Account")
                                if viewModel.email != nil {
                                    Text(viewModel.email ?? "")
                                        .font(.caption)
                                        .foregroundColor(.gray)
                                }
                            }
                            Spacer()
                            if viewModel.isAuthenticated {
                                Button(action: {
                                    if viewModel.signOut() {
                                        errorMessage = ""
                                        showingAlert = false
                                    } else {
                                        errorMessage = "Failed to Sign Out"
                                        showingAlert = true
                                    }
                                }) {
                                    Text("Settings.SignOut")
                                        .frame(height: 10)
                                        .padding()
                                        .foregroundColor(.white)
                                        .background(
                                            RoundedRectangle(cornerRadius: 25)
                                            .fill(Color("NavBar_Background"))
                                        )
                                }
                                .padding(.vertical, 8)
                                    .padding(.horizontal)
                                .buttonStyle(PlainButtonStyle())
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
                                .padding(.vertical, 8)
                                    .padding(.horizontal)
                            }
                        }
                        
                        Divider().padding(.horizontal)
                        
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
