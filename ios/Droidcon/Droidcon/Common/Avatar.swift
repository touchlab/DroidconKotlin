import SwiftUI
import Kingfisher

struct Avatar: View {
    private(set) var url: URL

    private(set) var size: CGFloat

    var body: some View {
        KFImage(url)
            .resizable()
            .scaledToFill()
            .frame(width: size, height: size)
            .aspectRatio(1, contentMode: .fill)
            .clipped()
            .cornerRadius(.greatestFiniteMagnitude)
    }
}

struct Avatar_Previews: PreviewProvider {
    static var previews: some View {
        Avatar(
            url: URL(string: "https://images.pexels.com/photos/2690323/pexels-photo-2690323.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=150&w=160")!,
            size: 72
        )
    }
}
