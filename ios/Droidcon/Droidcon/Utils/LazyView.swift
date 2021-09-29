import SwiftUI

struct LazyView<Content: View>: View {
    private let build: () -> Content

    init(@ViewBuilder _ build: @escaping () -> Content) {
        self.build = build
    }

    var body: Content {
        build()
    }
}

#if DEBUG
struct LazyView_Previews: PreviewProvider {
    static var previews: some View {
        LazyView() {
            Text("Build lazy view.")
        }
    }
}
#endif
