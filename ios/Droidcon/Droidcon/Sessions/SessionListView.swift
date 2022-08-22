import SwiftUI
import DroidconKit

struct SessionListView: View {
    private var component: BaseSessionListComponent
    private var navigationTitle: LocalizedStringKey
    
    @ObservedObject
    private var observableStack: ObservableValue<ChildStack<AnyObject, BaseSessionListComponentChild>>
    
    private var stack: ChildStack<AnyObject, BaseSessionListComponentChild> { observableStack.value }
    
    init(component: BaseSessionListComponent, navigationTitle: LocalizedStringKey) {
        self.component = component
        self.navigationTitle = navigationTitle
        self.observableStack = ObservableValue(component.stack)
    }
    
    var body: some View {
        NavigationView {
            VStack {
                switch stack.active.instance {
                case is BaseSessionListComponentChildLoading: EmptyView()
                case let child as BaseSessionListComponentChildDays: SessionDaysView(child.component)
                case is BaseSessionListComponentChildEmpty: ZeroCaseView()
                default: EmptyView()
                }
            }.navigationBarTitle(Text(navigationTitle), displayMode: .inline)
        }
    }
}
