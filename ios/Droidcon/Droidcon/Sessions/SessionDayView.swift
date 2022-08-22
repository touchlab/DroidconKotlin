import SwiftUI
import DroidconKit

struct SessionDayView: View {
    private var component: SessionDayComponent
    
    @ObservedObject
    private var observableModel: ObservableValue<SessionDayComponent.Model>
    
    private var viewModel: SessionDayComponent.Model { observableModel.value }
    
    init(_ component: SessionDayComponent) {
        self.component = component
        self.observableModel = ObservableValue(component.model)
    }
    
    var body: some View {
        ScrollView {
            LazyVStack {
                ForEach(viewModel.blocks, id: \.self) { sessionBlock in
                    SessionBlockView(
                        viewModel: sessionBlock,
                        onSessionTapped: component.itemSelected
                    )
                }
            }.padding(.top, 16)
        }
    }
}

struct SessionListView_Previews: PreviewProvider {
    static var previews: some View {
        //        SessionListView()
        EmptyView()
    }
}
