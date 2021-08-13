import SwiftUI
import DroidconKit

struct FeedbackDialog: View {
    @ObservedObject
    private(set) var viewModel: FeedbackDialogViewModel

    @Environment(\.colorScheme)
    private var colorScheme

    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                VStack(spacing: 0) {
                    Text("Titulek")
                        .font(.system(size: 20))
                        .fontWeight(.medium)
                        .padding(.horizontal)
                }
                .padding()

                HStack(spacing: 16) {
                    ForEach([FeedbackDialogViewModel.Rating.dissatisfied, FeedbackDialogViewModel.Rating.normal, FeedbackDialogViewModel.Rating.satisfied], id: \.self) { rating in
                        ratingButton(rating: rating, selectedRating: $viewModel.rating)
                    }
                }
                .padding([.horizontal, .bottom], 8)

                TextView($viewModel.comment, placeholder: "Feedback.Dialog.Opinion.Placeholder")
                    .enableScrolling(true)
                    .frame(maxHeight: 100)
                    .padding(8)
                    .background(Color("TextFieldBackground").cornerRadius(4))
                    .padding([.horizontal, .bottom])

                Divider()

                VStack(spacing: 0) {
                    Button(action: viewModel.submit) {
                        Text("Feedback.Dialog.Submit")
                            .font(.system(size: 18))
                            .fontWeight(.medium)
                            .foregroundColor(.blue)
                            .frame(maxWidth: .infinity)
                            .padding(12)
                    }
                    .disabled(viewModel.isSubmitDisabled)
                    .opacity(viewModel.isSubmitDisabled ? 0.5 : 1)
                    .padding(4)

                    Divider()

                    Button(action: viewModel.closeAndDisable) {
                        Text("Feedback.Dialog.CloseAndDisable")
                            .font(.system(size: 18))
                            .fontWeight(.medium)
                            .foregroundColor(.blue)
                            .frame(maxWidth: .infinity)
                            .padding(12)
                    }
                    .padding(4)

                    Divider()

                    Button(action: viewModel.skip) {
                        Text("Feedback.Dialog.Skip")
                            .font(.system(size: 18))
                            .fontWeight(.semibold)
                            .foregroundColor(.blue)
                            .frame(maxWidth: .infinity)
                            .padding(12)
                    }
                    .padding(4)
                }
                .fixedSize(horizontal: false, vertical: true)
            }
            .background(VisualEffectView(effect: UIBlurEffect(style: .prominent)))
            .cornerRadius(20)
        }
        .padding(32)
    }

    private func ratingButton(rating: FeedbackDialogViewModel.Rating, selectedRating: Binding<FeedbackDialogViewModel.Rating?>) -> some View {
        let imageName: String
        switch rating {
        case .dissatisfied:
            imageName = "Feedback_Dissatisfied"
        case .normal:
            imageName = "Feedback_Normal"
        case .satisfied:
            imageName = "Feedback_Satisfied"
        default:
            fatalError("Unknown image for rating '\(rating)'.")
        }

        let isSelected = selectedRating.wrappedValue == rating

        return Image(imageName)
            .frame(width: 44, height: 44)
            .padding(4)
            .background((isSelected ? Color("Accent") : .clear).cornerRadius(4))
            .foregroundColor(isSelected ? .white : .primary)
            .onTapGesture {
                selectedRating.wrappedValue = rating
            }
    }
}

#if DEBUG
struct FeedbackDialog_Previews: PreviewProvider {
    static var previews: some View {
//        FeedbackDialog()
        EmptyView()
    }
}
#endif
