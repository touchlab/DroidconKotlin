import SwiftUI
import DroidconKit

struct SpeakerListItemView: View {
    private static let iconSize: CGFloat = 32

    @ObservedObject
    private(set) var viewModel: SpeakerListItemViewModel

    var body: some View {
        HStack(alignment: viewModel.bio == nil ? .center : .top) {
            if let avatarUrl = viewModel.avatarUrl.flatMap({ URL(string: $0.string) }) {
                Avatar(url: avatarUrl, size: Self.iconSize)
            } else {
                Image(systemName: "face.dashed")
                    .resizable()
                    .frame(width: Self.iconSize, height: Self.iconSize)
            }

            VStack(alignment: .leading, spacing: 8) {
                Text(viewModel.info)
                    .font(.footnote)
                    .lineLimit(2)
                    .foregroundColor(.gray)
                    .fixedSize(horizontal: false, vertical: true)

                if let bio = viewModel.bio {
                    Text(bio)
                        .font(.callout)
                        .fixedSize(horizontal: false, vertical: true)
                }
            }
            .padding(.leading, 8)
        }
    }
}

struct SpeakerListItemView_Previews: PreviewProvider {
    static var previews: some View {
//        SpeakerListItemView()
        EmptyView()
    }
}
