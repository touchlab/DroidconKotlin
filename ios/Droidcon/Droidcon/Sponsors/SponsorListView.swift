import SwiftUI
import DroidconKit

struct SponsorListView: View {
    private var component: SponsorListComponent
    
    @ObservedObject
    private var observableModel: ObservableValue<SponsorListComponent.Model>
    
    private var viewModel: SponsorListComponent.Model { observableModel.value }
    
    init(_ component: SponsorListComponent) {
        self.component = component
        self.observableModel = ObservableValue(component.model)
    }

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    ForEach(viewModel.groups, id: \.self) { sponsorGroup in
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
                                    SponsorGroupItemView(viewModel: sponsor, onTapped: { component.sponsorTapped(sponsor: sponsor) })
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
            .navigationBarTitle(Text("Sponsors.Title"), displayMode: .inline)
        }
    }
}

struct SponsorListView_Previews: PreviewProvider {
    static var previews: some View {
//        SponsorListView()
        EmptyView()
    }
}
