import SwiftUI
import Kingfisher
import DroidconKit

struct SpeakerListItemView: View {
    private static let iconSize: CGFloat = 32

    private(set) var bio: String?
    private(set) var avatarUrl: Url?
    private(set) var info: String

    var body: some View {
        HStack(alignment: bio == nil ? .center : .top) {
            if let avatarUrl = avatarUrl.flatMap({ URL(string: $0.string) }) {
                Avatar(url: avatarUrl, size: Self.iconSize)
            } else {
                Image(systemName: "face.dashed")
                    .resizable()
                    .frame(width: Self.iconSize, height: Self.iconSize)
            }

            VStack(alignment: .leading, spacing: 8) {
                Text(info)
                    .font(.footnote)
                    .lineLimit(2)
                    .foregroundColor(.gray)
                    .fixedSize(horizontal: false, vertical: true)

                if let bio = bio {
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
