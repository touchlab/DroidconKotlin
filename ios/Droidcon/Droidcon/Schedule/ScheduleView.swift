import SwiftUI
import DroidconKit

struct ScheduleView: View {
    @ObservedObject
    private(set) var viewModel: BaseSessionListViewModel

    private(set) var navigationTitle: LocalizedStringKey

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
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
                        .navigationTitle(navigationTitle)
                        .navigationBarTitleDisplayMode(.inline)
                    }
                }


                SwitchingNavigationLink(
                    selection: $viewModel.presentedSessionDetail,
                    isActive: { $0 },
                    content: SessionDetailView.init(viewModel:)
                )
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
