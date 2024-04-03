import SwiftUI
import DroidconKit

struct MainView: View {
    
    @ObservedObject
    private(set) var viewModel: ApplicationViewModel

    var body: some View {
        TabView(selection: $viewModel.selectedTab) {
            ForEach(viewModel.tabs, id: \.self) { tab in
                switch (tab) {
                case ApplicationViewModel.Tab.schedule:
                    ScheduleView(
                        viewModel: viewModel.schedule,
                        navigationTitle: "Schedule.Title"
                    )
                    .tabItem {
                        Image(systemName: "calendar")
                        Text("Schedule.TabItem.Title")
                    }
                    .tag(tab);
                case ApplicationViewModel.Tab.myagenda:
                    ScheduleView(
                        viewModel: viewModel.agenda,
                        navigationTitle: "Agenda.Title"
                    )
                    .tabItem {
                        Image(systemName: "clock")
                        Text("Agenda.TabItem.Title")
                    }
                    .tag(tab);
                case ApplicationViewModel.Tab.sponsors:
                    SponsorListView(
                        viewModel: viewModel.sponsors,
                        navigationTitle: "Sponsors.Title"
                    )
                    .tabItem {
                        Image(systemName: "flame")
                        Text("Sponsors.TabItem.Title")
                    }
                    .tag(tab);
                case ApplicationViewModel.Tab.settings:
                    SettingsView(
                        viewModel: viewModel.settings
                    )
                    .tabItem {
                        Image(systemName: "gearshape")
                        Text("Settings.TabItem.Title")
                    }
                    .tag(tab);
                case ApplicationViewModel.Tab.chat:
                    ChatView()
                    .tabItem {
                        Image(systemName: "message.fill")
                        Text("Chat.TabItem.Title")
                    }
                    .tag(tab);
                default:
                    fatalError("Unknown tab \(tab).")
                }
            }
        }
        .accentColor(Color("Accent"))
        .present(item: $viewModel.presentedFeedback) { viewModel in
            FeedbackDialog(viewModel: viewModel)
        }
    }
}
