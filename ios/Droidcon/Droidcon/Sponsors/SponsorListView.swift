import SwiftUI
import DroidconKit

struct SponsorListView: View {
    @ObservedObject
    private(set) var viewModel: SponsorListViewModel

    private(set) var navigationTitle: LocalizedStringKey

    var body: some View {
        NavigationView {
            ScrollView {
                SwitchingNavigationLink(
                    selection: $viewModel.presentedSponsorDetail,
                    content: SponsorDetailView.init(viewModel:)
                )

                VStack(spacing: 20) {
                    ForEach(viewModel.sponsorGroups) { sponsorGroup in
                        VStack(spacing: 8) {
                            Text(sponsorGroup.title)
                                .font(.title)
                                .bold()
                                .multilineTextAlignment(.leading)
                                .frame(maxWidth: .infinity, alignment: .leading)

                            LazyVGrid(
                                columns: Array(repeating: GridItem(.flexible()), count: sponsorGroup.isProminent ? 3 : 4),
                                alignment: .center,
                                spacing: 8
                            ) {
                                ForEach(sponsorGroup.sponsors, id: \.self) { sponsor in
                                    SponsorGroupItemView(viewModel: sponsor)
                                }
                            }
                        }
                        .padding()
                        .background(
                            Color("ElevatedBackground")
                                .shadow(color: Color("Shadow"), radius: 2, y: 1)
                        )
                    }
                }
            }
            .frame(maxHeight: .infinity, alignment: .top)
            .navigationTitle(navigationTitle)
            .navigationBarTitleDisplayMode(.inline)
            .onChange(of: viewModel.presentedUrl) { url in
                guard let url = url else { return }
                if let nativeUrl = URL(string: url.string) {
                    UIApplication.shared.open(nativeUrl)
                }
                viewModel.presentedUrl = nil
            }
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}

struct SponsorListView_Previews: PreviewProvider {
    static var previews: some View {
//        SponsorListView()
        EmptyView()
    }
}
