import SwiftUI
import DroidconKit

struct SessionBlockView: View {
    static let cornerRadius: CGFloat = 8

    @ObservedObject
    private(set) var viewModel: SessionBlockViewModel

    private(set) var showAttendingIndicators: Bool

    var body: some View {
        ZStack {
            HStack(alignment: .top) {
                Text(viewModel.time)
                    .lineLimit(1)
                    .minimumScaleFactor(0.65)
                    .padding(.leading, 16)
                    .padding(.top, 4)
                    .frame(width: 80, alignment: .trailing)

                VStack(spacing: 0) {
                    ForEach(Array(viewModel.sessions.enumerated()), id: \.element) { index, session in
                        SessionBlockItemView(
                            viewModel: session,
                            showAttendingIndicators: showAttendingIndicators,
                            isFirstInBlock: index == viewModel.sessions.startIndex,
                            isLastInBlock: index == viewModel.sessions.endIndex - 1
                        )
                    }
                }
                .frame(maxWidth: .infinity, alignment: .top)
                .background(
                    RoundedCorners(color: Color.clear, tl: Self.cornerRadius, bl: Self.cornerRadius)
                        .shadow(color: Color("Shadow"), radius: 3, x: 1, y: 2)
                )
            }
            .padding(.bottom, 8)
        }
    }
}

struct SessionBlockView_Previews: PreviewProvider {
    static var previews: some View {
//        SessionBlockView()
        EmptyView()
    }
}
