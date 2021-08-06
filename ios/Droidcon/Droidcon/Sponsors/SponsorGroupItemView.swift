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
                        Text(viewModel.name)
                            .bold()
                    }
                    .resizable()
                    .scaledToFit()
                    .contentShape(Rectangle())
                    .onTapGesture {
                        viewModel.selected()
                    }
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
