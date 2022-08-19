import SwiftUI

struct ZeroCaseView: View {

    var body: some View {
        VStack(alignment: .center) {
            Text(NSLocalizedString("Shrug", comment: "Empty state"))
                .font(.system(size: 72))
                .minimumScaleFactor(0.65)
                .lineLimit(1)
                .padding()
        }
    }
}
