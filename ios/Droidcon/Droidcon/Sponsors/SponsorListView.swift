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
                    NavigationLink(
                        destination: SponsorsComposeController(viewModel: viewModel)
                    ) {
                        Text("Try out in Compose for iOS!")
                            .padding()
                            .frame(maxWidth: .infinity)
                    }
                    
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
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}

struct SponsorsComposeController: UIViewControllerRepresentable {
    
    let viewModel: SponsorListViewModel
    
    func makeUIViewController(context: Context) -> some UIViewController {
        SponsorsTestViewKt.getRootController(viewModel: viewModel)
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        uiViewController.view.setNeedsLayout()
    }
}

struct SponsorListView_Previews: PreviewProvider {
    static var previews: some View {
//        SponsorListView()
        EmptyView()
    }
}
