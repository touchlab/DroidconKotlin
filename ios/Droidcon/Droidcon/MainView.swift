import SwiftUI
import DroidconKit

struct MainView: View {
    private var component: ApplicationComponent
    
    @ObservedObject
    private var observableTabStack: ObservableValue<ChildStack<AnyObject, TabComponent>>
    
    @ObservedObject
    private var observableFeedbackStack: ObservableValue<ChildStack<AnyObject, ApplicationComponentFeedbackChild>>
    
    private var tabStack: ChildStack<AnyObject, TabComponent> { observableTabStack.value }
    private var feedbackStack: ChildStack<AnyObject, ApplicationComponentFeedbackChild> { observableFeedbackStack.value }

    init(_ component: ApplicationComponent) {
        self.component = component
        self.observableTabStack = ObservableValue(component.tabStack)
        self.observableFeedbackStack = ObservableValue(component.feedbackStack)
    }
    
    var body: some View {
        VStack {
            TabChildView(tabStack.active.instance)
                .frame(maxHeight: .infinity)
            
            let tab = tabStack.active.instance.tab
            
            HStack(spacing: 16) {
                TabItemView(text: "Schedule.TabItem.Title", systemImage: "calendar", isSelected: tab == .schedule, action: { component.selectTab(tab: .schedule) })

                TabItemView(text: "Agenda.TabItem.Title", systemImage: "clock", isSelected: tab == .agenda, action: { component.selectTab(tab: .agenda) })

                TabItemView(text: "Sponsors.TabItem.Title", systemImage: "flame", isSelected: tab == .sponsors, action: { component.selectTab(tab: .sponsors) })

                TabItemView(text: "Settings.TabItem.Title", systemImage: "gearshape", isSelected: tab == .settings, action: { component.selectTab(tab: .settings) })
            }
        }.present(item: Binding(get: { feedbackStack.active.instance as? ApplicationComponentFeedbackChildFeedback }, set: { _,_ in })) {
            FeedbackDialog($0.component)
        }
    }
}

private struct TabItemView: View {
    private(set) var text: LocalizedStringKey
    private(set) var systemImage: String
    private(set) var isSelected: Bool
    private(set) var action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Label(text, systemImage: systemImage)
        }.labelStyle(VerticalLabelStyle())
            .opacity(isSelected ? 1 : 0.5)
    }
}

private struct VerticalLabelStyle: LabelStyle {
    func makeBody(configuration: Configuration) -> some View {
        VStack(alignment: .center, spacing: 8) {
            configuration.icon
            configuration.title
        }
    }
}
