import SwiftUI
import DroidconKit

struct MainView: View {
    @State
    var selectedTabIndex: Int = 1

    @ObservedObject
    private(set) var viewModel: ApplicationViewModel

    var body: some View {
        TabView(selection: $selectedTabIndex) {
            ScheduleView(
                viewModel: viewModel.schedule,
                navigationTitle: "Schedule.Title"
            )
            .tabItem {
                Image(systemName: "calendar")
                Text("Schedule.TabItem.Title")
            }
            .tag(1)

            ScheduleView(
                viewModel: viewModel.agenda,
                navigationTitle: "Agenda.Title"
            )
            .tabItem {
                Image(systemName: "clock")
                Text("Agenda.TabItem.Title")
            }
            .tag(2)

            SponsorListView(
                viewModel: viewModel.sponsors,
                navigationTitle: "Sponsors.Title"
            )
            .tabItem {
                Image(systemName: "flame")
                Text("Sponsors.TabItem.Title")
            }
            .tag(3)

            SettingsView(
                viewModel: viewModel.settings
            )
            .tabItem {
                Image(systemName: "gearshape")
                Text("Settings.TabItem.Title")
            }
            .tag(4)
        }
        .attach(viewModel: viewModel)
    }
}
