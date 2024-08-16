import SwiftUI
import DroidconKit

struct SponsorGroupItemView: View {
    @ObservedObject
    private(set) var viewModel: SponsorGroupItemViewModel

    var body: some View {
        ZStack(alignment: .center) {
            Rectangle()
                .aspectRatio(1, contentMode: .fill)
                .foregroundColor(Color.white)
                .cornerRadius(.greatestFiniteMagnitude)
                .shadow(color: Color("Shadow"), radius: 2, y: 1)

            if let imageUrl = URL(string: viewModel.imageUrl.string) {
                AsyncImage(url: imageUrl) { image in
                        image.resizable()
                    } placeholder: {
                        placeholder
                    }
                    .scaledToFit()
                    .cornerRadius(.greatestFiniteMagnitude)
                    .padding(4)
            } else {
                placeholder
                    .padding(4)
            }
        }
        .contentShape(Rectangle())
        .onTapGesture {
            viewModel.selected()
        }
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
