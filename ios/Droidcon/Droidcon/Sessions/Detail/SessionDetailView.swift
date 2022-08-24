import SwiftUI
import DroidconKit

struct SessionDetailView: View {
    private static let iconSize: CGFloat = 24

    @ObservedObject
    private(set) var viewModel: SessionDetailViewModel

    var body: some View {
        ScrollView {
            ZStack {
                SwitchingNavigationLink(
                    selection: $viewModel.presentedSpeakerDetail,
                    content: SpeakerDetailView.init(viewModel:)
                )

                VStack(spacing: 0) {
                    VStack(alignment: .leading, spacing: 16) {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(viewModel.title)
                                .font(.title2)

                            Text(viewModel.info)
                                .font(.footnote)
                        }
                        .padding(.horizontal, 8)
                        .frame(maxWidth: .infinity, alignment: .leading)

                        if viewModel.state != .ended {
                            Button(action: viewModel.attendingTapped) {
                                if viewModel.isAttendingLoading {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .black))
                                } else {
                                    Image(systemName: viewModel.isAttending ? "checkmark" : "plus")
                                        .resizable()
                                        .foregroundColor(.black)
                                }
                            }
                            .frame(width: 16, height: 16)
                            .padding(12)
                            .background(Color("AttendButton"))
                            .cornerRadius(.greatestFiniteMagnitude)
                            .shadow(color: Color("Shadow"), radius: 2)
                            .padding(.bottom, -36)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(
                        Color("ElevatedHeaderBackground")
                            .shadow(color: Color("Shadow"), radius: 2, y: 1)
                    )

                    VStack(spacing: 16) {
                        if let sessionStateMessage = viewModel.state.flatMap(stateMessage(from:)) {
                            label(
                                Text(sessionStateMessage),
                                image: Image(systemName: "info.circle")
                            )
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal)
                        }

                        if viewModel.showFeedbackOption {
                            Button(action: viewModel.writeFeedbackTapped) {
                                if viewModel.feedbackAlreadyWritten {
                                    Text("Session.Detail.ChangeFeedback")
                                } else {
                                    Text("Session.Detail.AddFeedback")
                                }
                            }
                            .buttonStyle(FilledButtonStyle())
                        }

                        if let abstract = viewModel.abstract {
                            label(
                                Text(abstract),
                                image: Image(systemName: "doc.text")
                            )
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal)
                        }

                        VStack(spacing: 4) {
                            Section(header: VStack(spacing: 4) {
                                Text("Session.Detail.Speakers").font(.title2)
                                Divider()
                            }) {
                                ForEach(viewModel.speakers) { speaker in
                                    SpeakerListItemView(viewModel: speaker)
                                        .frame(maxWidth: .infinity, alignment: .leading)
                                        .padding(12)
                                        .contentShape(Rectangle())
                                        .onTapGesture {
                                            speaker.selected()
                                        }
                                }
                            }
                        }
                        .padding(4)
                        .padding(.top)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.top, 32)
                }
            }
            .present(item: $viewModel.presentedFeedback) { viewModel in
                FeedbackDialog(viewModel: viewModel)
            }
        }
        .navigationTitle("Session.Detail.Title")
    }

    private func label(_ text: Text, image: Image) -> some View {
        return HStack(alignment: .firstTextBaseline) {
            image
                .frame(width: Self.iconSize, height: Self.iconSize)

            text
                .font(.callout)
                .padding(.leading, 8)
                .fixedSize(horizontal: false, vertical: true)
        }
    }

    private func stateMessage(from state: SessionDetailViewModel.SessionState) -> LocalizedStringKey? {
        switch state {
        case .inconflict:
            return "Session.Detail.State.Conflict"
        case .inprogress:
            return "Session.Detail.State.InProgress"
        case .ended:
            return "Session.Detail.State.Ended"
        default:
            return nil
        }
    }
}

struct SessionDetailView_Previews: PreviewProvider {
    static var previews: some View {
//        SessionDetailView()
        EmptyView()
    }
}
