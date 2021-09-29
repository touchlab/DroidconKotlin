import SwiftUI
import Kingfisher
import DroidconKit

struct SponsorDetailView: View {
    private static let iconSize: CGFloat = 24

    @ObservedObject
    private(set) var viewModel: SponsorDetailViewModel

    var body: some View {
        ScrollView {
            ZStack {
                SwitchingNavigationLink(
                    selection: $viewModel.presentedSpeakerDetail,
                    isActive: { $0 },
                    content: SpeakerDetailView.init(viewModel:)
                )

                VStack(spacing: 0) {
                    HStack(spacing: 16) {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(viewModel.name)
                                .font(.title2)

                            Text(viewModel.groupName)
                                .font(.footnote)
                        }
                        .padding(.horizontal, 8)
                        .frame(maxWidth: .infinity, alignment: .leading)

                        if let imageUrl = URL(string: viewModel.imageUrl.string) {
                            KFImage(imageUrl)
                                .placeholder {
                                    GeometryReader { geometry in
                                        Text(viewModel.name)
                                            .bold()
                                            .lineLimit(1)
                                            .minimumScaleFactor(0.65)
                                            .padding(8)
                                            .frame(maxWidth: .infinity)
                                            .frame(height: geometry.size.width)
                                    }
                                }
                                .resizable()
                                .scaledToFit()
                                .padding(4)
                                .background(Color.white)
                                .cornerRadius(.greatestFiniteMagnitude)
                                .shadow(color: Color("Shadow"), radius: 2, y: 1)
                                .frame(idealWidth: 128, idealHeight: 128)
                        }
                    }
                    .padding()
                    .background(
                        Color("ElevatedHeaderBackground")
                            .shadow(color: Color("Shadow"), radius: 2, y: 1)
                    )

                    VStack(spacing: 16) {
                        if let abstract = viewModel.abstract {
                            HStack(alignment: .firstTextBaseline) {
                                Image(systemName: "doc.text")
                                    .frame(width: Self.iconSize, height: Self.iconSize)

                                TextView(.constant(abstract))
                                    .isEditable(false)
                                    .autoDetectDataTypes(.link)
                                    .font(Font.callout)
                                    .padding(.leading, 8)
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal)
                        }

                        if !viewModel.representatives.isEmpty {
                            VStack(spacing: 4) {
                                Section(header: VStack(spacing: 4) {
                                    Text("Sponsor.Detail.Representatives").font(.title2)
                                    Divider()
                                }) {
                                    ForEach(viewModel.representatives) { representative in
                                        SpeakerListItemView(viewModel: representative)
                                            .frame(maxWidth: .infinity, alignment: .leading)
                                            .padding(12)
                                            .contentShape(Rectangle())
                                            .onTapGesture {
                                                representative.selected()
                                            }
                                    }
                                }
                            }
                            .padding(4)
                            .padding(.top)
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.top, 32)
                }
            }
        }
        .navigationTitle("Sponsor.Detail.Title")
    }

    private func label(_ text: Text, image: Image) -> some View {
        return HStack(alignment: .firstTextBaseline) {
            image
                .frame(width: Self.iconSize, height: Self.iconSize)

            text
                .font(.callout)
                .padding(.leading, 8)
                .fixedSize(horizontal: false, vertical: true)
        }
    }
}

struct SponsorDetailView_Previews: PreviewProvider {
    static var previews: some View {
        EmptyView()
//        SponsorDetailView()
    }
}
