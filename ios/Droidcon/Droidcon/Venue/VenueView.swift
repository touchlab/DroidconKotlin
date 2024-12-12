import SwiftUI
import DroidconKit

struct VenueView: View {
    var body: some View {
        VenueBodyView()
    }
}

private struct VenueBodyView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> some UIViewController {
        venueBodyViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}
