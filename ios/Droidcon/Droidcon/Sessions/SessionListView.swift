import SwiftUI
import DroidconKit

struct SessionListView: View {
    @ObservedObject
    private(set) var viewModel: SessionDayViewModel

    private(set) var showAttendingIndicators: Bool

    var body: some View {
        LazyVStack {
            ForEach(viewModel.blocks) { sessionBlock in
                SessionBlockView(
                    viewModel: sessionBlock,
                    showAttendingIndicators: showAttendingIndicators
                )
            }
        }
    }
}

struct SessionListView_Previews: PreviewProvider {
    static var previews: some View {
//        SessionListView()
        EmptyView()
    }
}
