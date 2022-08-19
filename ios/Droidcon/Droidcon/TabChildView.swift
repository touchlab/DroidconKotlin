import SwiftUI
import DroidconKit

struct TabChildView: View {
    private let component: TabComponent
    
    @ObservedObject
    private var observableStack: ObservableValue<ChildStack<AnyObject, TabComponentChild>>
    
    private var stack: ChildStack<AnyObject, TabComponentChild> { observableStack.value }
    
    init(_ component: TabComponent) {
        self.component = component
        self.observableStack = ObservableValue(component.stack)
    }
    
    var body: some View {
        switch stack.active.instance {
        case let child as TabComponentChildMainSchedule: SessionListView(component: child.component, navigationTitle: "Schedule.Title")
        case let child as TabComponentChildMainAgenda: SessionListView(component: child.component, navigationTitle: "Agenda.Title")
        case let child as TabComponentChildMainSponsors: SponsorListView(child.component)
        case let child as TabComponentChildMainSettings: SettingsView(child.component)
        case let child as TabComponentChildSession: SessionDetailView(child.component)
        case let child as TabComponentChildSponsor: SponsorDetailView(child.component)
        case let child as TabComponentChildSpeaker: SpeakerDetailView(component: child.component)
        default: EmptyView()
        }
    }
}
