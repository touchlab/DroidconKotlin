import SwiftUI
import DroidconKit

struct AboutView: View {
    private static let iconSize: CGFloat = 32

    private var component: AboutComponent
    
    @ObservedObject
    private var observableModel: ObservableValue<AboutComponent.Model>
    
    private var viewModel: AboutComponent.Model { observableModel.value }
    
    init(_ component: AboutComponent) {
        self.component = component
        self.observableModel = ObservableValue(component.model)
    }

    var body: some View {
        VStack(spacing: 16) {
            ForEach(Array(viewModel.items.enumerated()), id: \.element) { index, section in
                paragraph {
                    Text(section.title)
                        .font(.headline)
                        .padding(.bottom, 4)

                    ForEach(section.detail.split(separator: "\n").filter { !$0.isEmpty }.map(String.init), id: \.self) { text in
                        TextView(.constant(text))
                            .isEditable(false)
                            .autoDetectDataTypes(.link)
                            .font(Font.callout)
                    }

                    Image(section.icon)
                        .resizable()
                        .scaledToFit()
                        .frame(maxHeight: 50)
                }
            }
        }
        .padding([.top, .leading], 8)
        .padding([.trailing, .bottom])
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
