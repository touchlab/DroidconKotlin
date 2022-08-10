import SwiftUI
import DroidconKit

struct SpeakerDetailView: View {
    private static let avatarSize: CGFloat = 48

    @ObservedObject
    private(set) var viewModel: SpeakerDetailViewModel

    var body: some View {
        VStack(spacing: 0) {
            NavigationLink(
                destination: SpeakerDetailComposeController(viewModel: viewModel)
            ) {
                Text("Try out in Compose for iOS!")
                    .padding()
                    .frame(maxWidth: .infinity)
            }
            
            ScrollView {
                HStack(spacing: 16) {
                    if let avatarUrl = viewModel.avatarUrl.flatMap({ URL(string: $0.string) }) {
                        Avatar(
                            url: avatarUrl,
                            size: Self.avatarSize
                        )
                    } else {
                        Image(systemName: "face.dashed")
                            .resizable()
                            .frame(width: Self.avatarSize, height: Self.avatarSize)
                    }

                    VStack(alignment: .leading, spacing: 4) {
                        Text(viewModel.name)
                            .font(.title2)

                        if let position = viewModel.position {
                            Text(position)
                                .font(.footnote)
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                }
                .padding()
                .background(
                    Color("ElevatedHeaderBackground")
                        .shadow(color: Color("Shadow"), radius: 2, y: 1)
                )

                VStack(spacing: 16) {
                    if let website = viewModel.socials.website?.string, let websiteUrl = URL(string: website) {
                        largeImageLabel(
                            Link(website, destination: websiteUrl).font(.callout),
                            image: Image(systemName: "globe")
                        )
                        .frame(maxWidth: .infinity, alignment: .leading)
                    }

                    if let twitter = viewModel.socials.twitter?.string, let twitterUrl = URL(string: twitter) {
                        largeImageLabel(
                            Link(twitter, destination: twitterUrl).font(.callout),
                            image: Image("twitter")
                        )
                        .frame(maxWidth: .infinity, alignment: .leading)
                    }

                    if let linkedIn = viewModel.socials.linkedIn?.string, let linkedInUrl = URL(string: linkedIn) {
                        largeImageLabel(
                            Link(linkedIn, destination: linkedInUrl).font(.callout),
                            image: Image("linked-in")
                        )
                        .frame(maxWidth: .infinity, alignment: .leading)
                    }

                    // Show the divider only if there is at least 1 social link and a bio.
                    if !viewModel.socials.isEmpty && !(viewModel.bio.map { $0.isEmpty } ?? true) {
                        Divider()
                    }

                    if let bio = viewModel.bio {
                        label(
                            Text(bio).font(.callout),
                            image: Image(systemName: "doc.text")
                        )
                        .frame(maxWidth: .infinity, alignment: .leading)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding()
            }
        }
        .navigationTitle("Speaker.Detail.Title")
    }

    private func largeImageLabel<TEXT: View>(_ text: TEXT, image: Image) -> some View {
        return label(
            text,
            image: image
                .resizable()
                .scaledToFit(),
            alignment: .top
        )
    }

    private func label<TEXT: View, IMAGE: View>(_ text: TEXT, image: IMAGE, alignment: VerticalAlignment = .firstTextBaseline) -> some View {
        return HStack(alignment: alignment) {
            image
                .frame(width: 24, height: 24)

            text
                .padding(.leading, 8)
                .padding(.top, 4)
        }
    }
}

struct SpeakerDetailView_Previews: PreviewProvider {
    static var previews: some View {
//        SpeakerDetailView()
        EmptyView()
    }
}

struct SpeakerDetailComposeController: UIViewControllerRepresentable {
    
    let viewModel: SpeakerDetailViewModel
    
    func makeUIViewController(context: Context) -> some UIViewController {
        SpeakerDetailTestViewKt.getRootController(viewModel: viewModel)
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}
