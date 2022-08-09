import SwiftUI
import DroidconKit

struct ScheduleView: View {
    @ObservedObject
    private(set) var viewModel: BaseSessionListViewModel

    private(set) var navigationTitle: LocalizedStringKey

    @State
    private var shouldShowShrug: Bool = false

    var body: some View {
        NavigationView {
            GeometryReader { geometry in
                VStack(spacing: 0) {
                    NavigationLink(destination: ScheduleComposeController(viewModel: viewModel).frame(height: geometry.size.height)) {
                        Text("Try out in Compose for iOS!")
                            .padding()
                            .frame(maxWidth: .infinity)
                    }
                    
                    if let days = viewModel.days {
                        if !days.isEmpty {
                            DaySelectionView(viewModel: viewModel)
                                .padding()
                                .background(
                                    Color("ElevatedHeaderBackground")
                                        .shadow(color: Color("Shadow"), radius: 2, y: 1)
                                )
                                // To overshadow the scroll view.
                                .zIndex(1)

                            ScrollView {
                                if let selectedDay = viewModel.selectedDay {
                                    SessionListView(
                                        viewModel: selectedDay,
                                        showAttendingIndicators: !viewModel.attendingOnly
                                    )
                                    .padding(.top)
                                }
                            }
                        } else {
                            Spacer().frame(maxHeight: 128)

                            Text(NSLocalizedString("Shrug", comment: "Empty list state"))
                                .font(.system(size: 72))
                                .minimumScaleFactor(0.65)
                                .lineLimit(1)
                                .padding()
                                .opacity(shouldShowShrug ? 1 : 0)
                                .onAppear {
                                    // Waiting for the next loop results in a nicer animation.
                                    DispatchQueue.main.async {
                                        withAnimation {
                                            shouldShowShrug = true
                                        }
                                    }
                                }
                        }
                    } else {
                        Spacer().frame(maxHeight: 128)

                        ProgressView()
                            .scaleEffect(x: 2, y: 2)
                    }

                    SwitchingNavigationLink(
                        selection: $viewModel.presentedSessionDetail,
                        content: SessionDetailView.init(viewModel:)
                    )
                }
                .frame(maxHeight: .infinity, alignment: .top)
                .navigationTitle(navigationTitle)
                .navigationBarTitleDisplayMode(.inline)
            }
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}

struct ScheduleView_Previews: PreviewProvider {
    static var previews: some View {
        EmptyView()
    }
}

struct ScheduleComposeController: UIViewControllerRepresentable {
    
    let viewModel: BaseSessionListViewModel
    
    func makeUIViewController(context: Context) -> some UIViewController {
        SessionListTestViewKt.getRootController(viewModel: viewModel)
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}
