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
            VStack {
                paragraph {
                    Text("About.Touchlab.Title")
                        .font(.headline)

                    Text("About.Touchlab.Text.1")
                        .font(.callout)

                    Text("About.Touchlab.Text.2")
                        .font(.callout)

                    Link(Self.touchlabUrl.absoluteString, destination: Self.touchlabUrl)
                        .font(.callout)

                    Image("touchlab")
                        .resizable()
                        .scaledToFit()
                        .frame(maxHeight: 50)
                        .onTapGesture {
                            openURL(Self.touchlabUrl)
                        }
                }

                paragraph {
                    Text("About.DroidconApp.Title")
                        .font(.headline)

                    Text("About.DroidconApp.Text.1")
                        .font(.callout)

                    VStack(alignment: .leading, spacing: 0) {
                        Text("About.DroidconApp.Text.2")
                            .font(.callout)

                        Link(Self.droidconAppUrl.absoluteString, destination: Self.droidconAppUrl)
                            .font(.callout)
                    }

                    Image("kotlin")
                        .resizable()
                        .scaledToFit()
                        .frame(maxHeight: 50)
                        .onTapGesture {
                            openURL(Self.droidconAppUrl)
                        }
                }

                paragraph {
                    Text("About.Droidcon.Title")
                        .font(.headline)

                    Text("About.Droidcon.Text.1")
                        .font(.callout)

                    Text("About.Droidcon.Text.2")
                        .font(.callout)

                    Image("droidcon")
                        .resizable()
                        .scaledToFit()
                        .frame(maxHeight: 50)
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

            VStack(spacing: 12) {
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
