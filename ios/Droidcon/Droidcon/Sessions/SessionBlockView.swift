import SwiftUI
import DroidconKit

struct SessionBlockView: View {
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
                        if index != viewModel.sessions.startIndex {
                            Divider().padding(.vertical, 4)
                        }

                        HStack(spacing: 0) {
                            if !session.isServiceSession {
                                attendanceIndicator(for: session)
                                    .frame(width: 8, height: 8)
                                    .cornerRadius(.greatestFiniteMagnitude)
                                    .padding(.leading, -16)
                            }

                            VStack(spacing: 0) {
                                Text(session.title)
                                    .font(.headline)
                                    .bold()
                                    .lineLimit(2)
                                    .multilineTextAlignment(.leading)
                                    .frame(maxWidth: .infinity, alignment: .leading)
                                    .padding(.top, 4)
                                    .padding(.horizontal, 8)
                                    .padding(.bottom, session.isServiceSession ? 4 : 0)
                                    .fixedSize(horizontal: false, vertical: true)
                                if !session.isServiceSession {
                                    Text("by \(session.speakers)")
                                        .font(.subheadline)
                                        .lineLimit(2)
                                        .multilineTextAlignment(.leading)
                                        .frame(maxWidth: .infinity, alignment: .leading)
                                        .padding([.horizontal, .top], 8)
                                        .padding(.bottom, 4)
                                        .fixedSize(horizontal: false, vertical: true)
                                }
                            }
                        }
                        .contentShape(Rectangle())
                        .onTapGesture {
                            session.selected()
                        }
                    }
                }
                .padding(.vertical, 4)
                .frame(maxWidth: .infinity, alignment: .top)
                .background(
                    RoundedCorners(color: Color("ElevatedBackground"), tl: 8, tr: 0, bl: 8, br: 0)
                        .shadow(color: Color("Shadow"), radius: 3, x: 1, y: 2)
                )
            }
            .padding(.bottom, 8)
        }
    }

    private func attendanceIndicator(for session: SessionListItemViewModel) -> Color {
        // `guard` instead of `if` to sidestep the issue of SwiftUI not rendering the dot at all.
        guard showAttendingIndicators && session.isAttending else { return Color.clear }

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

struct SessionBlockView_Previews: PreviewProvider {
    static var previews: some View {
//        SessionBlockView()
        EmptyView()
    }
}
