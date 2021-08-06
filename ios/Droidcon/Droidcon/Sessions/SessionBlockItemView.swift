import SwiftUI
import DroidconKit

struct SessionBlockItemView: View {
    @ObservedObject
    private(set) var viewModel: SessionListItemViewModel

    private(set) var showAttendingIndicators: Bool

    private(set) var isFirstInBlock: Bool
    private(set) var isLastInBlock: Bool

    var body: some View {
        VStack(spacing: 0) {
            let backgroundColor = Color(viewModel.isInPast ? "ElevatedBackgroundDisabled" : "ElevatedBackground")

            if isFirstInBlock {
                RoundedCorners(color: backgroundColor, tl: SessionBlockView.cornerRadius)
                    .frame(height: SessionBlockView.cornerRadius * 2)
                    .padding(.bottom, -SessionBlockView.cornerRadius)
            }

            if !isFirstInBlock {
                Divider()
                    .background(Color("Divider"))
            }

            HStack(spacing: 0) {
                // BUG: Do not wrap this in an `if`, otherwise SwiftUI won't render it even if the condition is true.
                // It probably has to do with the negative padding, so if you have a better idea, go ahead.
                attendanceIndicator(for: viewModel)
                    .frame(width: 8, height: 8)
                    .cornerRadius(.greatestFiniteMagnitude)
                    .padding(.leading, -16)

                VStack(spacing: 0) {
                    Text(viewModel.title)
                        .font(.headline)
                        .bold()
                        .lineLimit(2)
                        .multilineTextAlignment(.leading)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .fixedSize(horizontal: false, vertical: true)
                    if !viewModel.isServiceSession {
                        Text("by \(viewModel.speakers)")
                            .font(.subheadline)
                            .lineLimit(2)
                            .multilineTextAlignment(.leading)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.top, 4)
                            .fixedSize(horizontal: false, vertical: true)
                    }
                }
                .padding(.vertical, 4)
                .padding(.leading, 8)
                .padding(.trailing)
                .padding(.top, isFirstInBlock ? 0 : 4)
                .padding(.bottom, isLastInBlock ? 0 : 4)
                .background(backgroundColor)
            }
            .contentShape(Rectangle())
            .onTapGesture {
                viewModel.selected()
            }
            .zIndex(1)

            if isLastInBlock {
                RoundedCorners(color: backgroundColor, bl: SessionBlockView.cornerRadius)
                    .frame(height: SessionBlockView.cornerRadius * 2)
                    .padding(.top, -SessionBlockView.cornerRadius)
            }
        }
    }

    private func attendanceIndicator(for session: SessionListItemViewModel) -> Color {
        // `guard` instead of `if` to sidestep the issue of SwiftUI not rendering the dot at all.
        guard showAttendingIndicators && !session.isServiceSession && session.isAttending else { return Color.clear }

        let colorName: String
        if session.isInPast {
            colorName = "AttendingPast"
        } else if session.isInConflict {
            colorName = "AttendingConflict"
        } else {
            colorName = "AttendingNormal"
        }

        return Color(colorName)
    }
}

struct SessionBlockItemView_Previews: PreviewProvider {
    static var previews: some View {
//        SessionBlockItemView()
        EmptyView()
    }
}
