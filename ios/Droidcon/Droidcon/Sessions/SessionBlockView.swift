import SwiftUI
import DroidconKit

struct SessionBlockView: View {
    static let cornerRadius: CGFloat = 8

    private(set) var viewModel: SessionDayComponent.ModelBlock
    private(set) var onSessionTapped: (SessionDayComponent.ModelItem) -> Void

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
                    ForEach(Array(viewModel.items.enumerated()), id: \.element) { index, session in
                        SessionBlockItemView(
                            viewModel: session,
                            isFirstInBlock: index == viewModel.items.startIndex,
                            isLastInBlock: index == viewModel.items.endIndex - 1,
                            onTapped: { onSessionTapped(session) }
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
