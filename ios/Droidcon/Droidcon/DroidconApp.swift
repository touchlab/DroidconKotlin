import SwiftUI
import DroidconKit

@main
struct DroidconApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate

    var body: some Scene {
        WindowGroup {
            MainView(viewModel: koin.applicationViewModel)
        }
    }
}

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

            SettingsView(
                viewModel: viewModel.settings
            )
            .tabItem {
                Image(systemName: "gearshape")
                Text("Settings.TabItem.Title")
            }
            .tag(3)
        }
        .attach(viewModel: viewModel)
    }
}
