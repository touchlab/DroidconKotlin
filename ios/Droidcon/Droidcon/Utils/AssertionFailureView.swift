import SwiftUI

struct AssertionFailureView: View {
    init(_ message: String) {
        assertionFailure(message)
    }

    var body: some View {
        Text("Internal error occurred. Please restart the application and try again.")
    }
}

#if DEBUG
struct AssertionFailureView_Previews: PreviewProvider {
    static var previews: some View {
        AssertionFailureView("Error.")
    }
}
#endif
