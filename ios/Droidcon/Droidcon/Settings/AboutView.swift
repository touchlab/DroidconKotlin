import SwiftUI
import DroidconKit

struct AboutView: View {
    private static let iconSize: CGFloat = 32
    private static let touchlabUrl = URL(string: "http://touchlab.co")!
    private static let droidconAppUrl = URL(string: "https://github.com/touchlab/DroidconKotlin")!

    @Environment(\.openURL)
    var openURL

    private(set) var viewModel: AboutViewModel

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                ForEach(Array(viewModel.items.enumerated()), id: \.element) { index, section in
                    paragraph {
                        Text(section.title)
                            .font(.headline)
                            .padding(.bottom, 4)

                        ForEach(section.detail.split(separator: "\n").filter { !$0.isEmpty }, id: \.self) { text in
                            Text(text)
                                .font(.callout)
                        }

                        if let link = section.link.flatMap({ URL(string: $0.string) }) {
                            Link(link.absoluteString, destination: link)
                                .font(.callout)
                        }

                        Image(section.icon)
                            .resizable()
                            .scaledToFit()
                            .frame(maxHeight: 50)
                            .onTapGesture {
                                if let link = section.link.flatMap({ URL(string: $0.string) }) {
                                    openURL(link)
                                }
                            }
                    }
                }
            }
            .padding([.top, .leading], 8)
            .padding([.trailing, .bottom])
        }
        .navigationTitle("About.Title")
    }

    @ViewBuilder
    private func paragraph<Content: View>(@ViewBuilder content: () -> Content) -> some View {
        HStack(alignment: .firstTextBaseline) {
            Image(systemName: "info.circle")
                .frame(width: Self.iconSize, height: Self.iconSize)

            VStack(spacing: 8) {
                content()
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
    }
}

struct AboutView_Previews: PreviewProvider {
    static var previews: some View {
//        AboutView()
        EmptyView()
    }
}
