import SwiftUI
import DroidconKit

struct SpeakerDetailView: View {
    private static let avatarSize: CGFloat = 48
    
    private(set) var component: SpeakerDetailComponent
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                ScrollView {
                    HStack(spacing: 16) {
                        if let avatarUrl = component.avatarUrl.flatMap({ URL(string: $0.string) }) {
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
                            Text(component.name)
                                .font(.title2)
                            
                            if let position = component.position {
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
                        if let website = component.socials.website?.string, let websiteUrl = URL(string: website) {
                            largeImageLabel(
                                Link(website, destination: websiteUrl).font(.callout),
                                image: Image(systemName: "globe")
                            )
                            .frame(maxWidth: .infinity, alignment: .leading)
                        }
                        
                        if let twitter = component.socials.twitter?.string, let twitterUrl = URL(string: twitter) {
                            largeImageLabel(
                                Link(twitter, destination: twitterUrl).font(.callout),
                                image: Image("twitter")
                            )
                            .frame(maxWidth: .infinity, alignment: .leading)
                        }
                        
                        if let linkedIn = component.socials.linkedIn?.string, let linkedInUrl = URL(string: linkedIn) {
                            largeImageLabel(
                                Link(linkedIn, destination: linkedInUrl).font(.callout),
                                image: Image("linked-in")
                            )
                            .frame(maxWidth: .infinity, alignment: .leading)
                        }
                        
                        // Show the divider only if there is at least 1 social link and a bio.
                        if !component.socials.isEmpty && !(component.bio.map { $0.isEmpty } ?? true) {
                            Divider()
                        }
                        
                        if let bio = component.bio {
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
            }.navigationBarTitle(Text("Speaker.Detail.Title"), displayMode: .inline)
                .navigationBarItems(
                    leading: Image(systemName: "arrow.backward")
                        .aspectRatio(contentMode: .fit)
                        .imageScale(.large)
                        .foregroundColor(.accentColor)
                        .onTapGesture(perform: component.backTapped)
                )
        }
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
