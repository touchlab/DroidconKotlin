import SwiftUI
import Kingfisher
import DroidconKit

struct SponsorGroupItemView: View {
    private(set) var viewModel: SponsorListComponent.ModelSponsor
    private(set) var onTapped: () -> Void

    var body: some View {
        ZStack(alignment: .center) {
            Rectangle()
                .aspectRatio(1, contentMode: .fill)
                .foregroundColor(Color.white)
                .cornerRadius(.greatestFiniteMagnitude)
                .shadow(color: Color("Shadow"), radius: 2, y: 1)

            if let imageUrl = viewModel.imageUrl.flatMap({ URL(string: $0) }) {
                KFImage(imageUrl)
                    .placeholder {
                        placeholder
                    }
                    .resizable()
                    .scaledToFit()
                    .cornerRadius(.greatestFiniteMagnitude)
                    .padding(4)
            } else {
                placeholder
                    .padding(4)
            }
        }
        .contentShape(Rectangle())
        .onTapGesture(perform: onTapped)
    }

    @ViewBuilder
    private var placeholder: some View {
        Text(viewModel.name)
            .foregroundColor(.black)
            .bold()
            .lineLimit(1)
            .minimumScaleFactor(0.65)
            .padding(8)
            .frame(maxWidth: .infinity)
    }
}

struct SponsorListItemView_Previews: PreviewProvider {
    static var previews: some View {
//        SponsorListItemView()
        EmptyView()
    }
}
