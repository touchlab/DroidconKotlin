import SwiftUI
import DroidconKit

struct SessionDaysView: View {
    private let component: SessionDaysComponent
    
    @ObservedObject
    private var observableStack: ObservableValue<ChildStack<AnyObject, SessionDayComponent>>
    
    private var stack: ChildStack<AnyObject, SessionDayComponent> { observableStack.value }
    
    init(_ component: SessionDaysComponent) {
        self.component = component
        self.observableStack = ObservableValue(component.stack)
    }
    
    var body: some View {
        VStack(spacing: 0) {
            Picker("", selection: Binding(get: { stack.active.instance.date }, set: component.selectTab)) {
                ForEach(component.days, id: \.self) { day in
                    Text(day.title)
                        .tag(day.date)
                }
            }
            .pickerStyle(SegmentedPickerStyle())
            .padding()
            .background(
                Color("ElevatedHeaderBackground")
                    .shadow(color: Color("Shadow"), radius: 2, y: 1)
            )
            // To overshadow the scroll view.
            .zIndex(1)

            SessionDayView(stack.active.instance)
        }
        .frame(maxHeight: .infinity, alignment: .top)
    }
}
