import SwiftUI
import DroidconKit

struct SponsorListView: View {
    @ObservedObject
    private(set) var viewModel: SponsorListViewModel

    private(set) var navigationTitle: LocalizedStringKey

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    ForEach(Array(viewModel.sponsorGroups.enumerated()), id: \.element) { index, sponsorGroup in
                        let isFirstGroup = index == viewModel.sponsorGroups.startIndex
                        VStack(spacing: 8) {
                            Text(sponsorGroup.title)
                                .font(.title)
                                .bold()
                                .multilineTextAlignment(.leading)
                                .frame(maxWidth: .infinity, alignment: .leading)

                            LazyVGrid(
                                columns: Array(repeating: GridItem(.flexible()), count: isFirstGroup ? 3 : 4),
                                alignment: .center,
                                spacing: 16
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
