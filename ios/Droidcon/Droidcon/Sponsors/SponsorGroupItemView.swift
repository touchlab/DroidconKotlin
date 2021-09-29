import SwiftUI
import Kingfisher
import DroidconKit

struct SponsorGroupItemView: View {
    @ObservedObject
    private(set) var viewModel: SponsorGroupItemViewModel

    var body: some View {
        ZStack {
            if let imageUrl = URL(string: viewModel.imageUrl.string) {
                KFImage(imageUrl)
                    .placeholder {
                        GeometryReader { geometry in
                            Text(viewModel.name)
                                .bold()
                                .lineLimit(1)
                                .minimumScaleFactor(0.65)
                                .padding(8)
                                .frame(maxWidth: .infinity)
                                .frame(height: geometry.size.width)
                        }
                    }
                    .resizable()
                    .scaledToFit()
                    .contentShape(Rectangle())
                    .onTapGesture {
                        viewModel.selected()
                    }
                    .padding(4)
                    .background(Color.white)
                    .cornerRadius(.greatestFiniteMagnitude)
                    .shadow(color: Color("Shadow"), radius: 2, y: 1)
            }
        }
    }
}

struct SponsorListItemView_Previews: PreviewProvider {
    static var previews: some View {
//        SponsorListItemView()
        EmptyView()
    }
}
