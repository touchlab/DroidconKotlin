import SwiftUI
import DroidconKit

struct MainView: View {
    
    @ObservedObject
    private(set) var viewModel: ApplicationViewModel

    var body: some View {
        TabView(selection: $viewModel.selectedTab) {
            ForEach(viewModel.tabs, id: \.self) { tab in
                switch (tab) {
                case .schedule:
                    ScheduleView(
                        viewModel: viewModel.schedule,
                        navigationTitle: "Schedule.Title"
                    )
                    .tabItem {
                        Image(systemName: "calendar")
                        Text("Schedule.TabItem.Title")
                    }
                    .tag(tab);
                case .myAgenda:
                    ScheduleView(
                        viewModel: viewModel.agenda,
                        navigationTitle: "Agenda.Title"
                    )
                    .tabItem {
                        Image(systemName: "clock")
                        Text("Agenda.TabItem.Title")
                    }
                    .tag(tab);
                case .sponsors:
                    SponsorListView(
                        viewModel: viewModel.sponsors,
                        navigationTitle: "Sponsors.Title"
                    )
                    .tabItem {
                        Image(systemName: "flame")
                        Text("Sponsors.TabItem.Title")
                    }
                    .tag(tab);
                case .settings:
                    SettingsView(
                        viewModel: viewModel.settings
                    )
                    .tabItem {
                        Image(systemName: "gearshape")
                        Text("Settings.TabItem.Title")
                    }
                    .tag(tab);
                case .venue:
                    VenueView()
                        .tabItem {
                            Image(systemName: "map")
                            Text("Venue.TabItem.Title")
                        }
                }
            }
        }
        .accentColor(Color("Accent"))
        .present(item: $viewModel.presentedFeedback) { viewModel in
            FeedbackDialog(viewModel: viewModel)
        }
    }
}
