import SwiftUI
import DroidconKit

struct VenueView: View {
    var body: some View {
        NavigationView {
            VenueBodyView()
                .navigationTitle("Venue.Title")
                .navigationBarTitleDisplayMode(.inline)
        }
    }
}

private struct VenueBodyView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> some UIViewController {
        venueBodyViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}
