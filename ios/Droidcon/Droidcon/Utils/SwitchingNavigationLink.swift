import SwiftUI

extension SwitchingNavigationLink where T == U {
    init(selection: Binding<T?>, @ViewBuilder content: @escaping (U) -> Content) {
        self.init(selection: selection, isActive: { $0 }, content: content)
    }
}

struct SwitchingNavigationLink<T, U, Content: View>: View {
    @Binding
    private var selection: T?
    private let isActive: (T) -> U?
    private let content: (U) -> Content

    init(selection: Binding<T?>, isActive: @escaping (T) -> U?, @ViewBuilder content: @escaping (U) -> Content) {
        self._selection = selection
        self.isActive = isActive
        self.content = content
    }

    var body: some View {
        NavigationLink(
            destination: LazyView { [self] in
                if let mappedSelection = self.selection.flatMap(self.isActive) {
                    content(mappedSelection)
                } else {
                    AssertionFailureView("Illegal state! Mapped selection is nil but the link is active!")
                }
            },
            isActive: Binding<Bool>(
                get: {
                    if selection.flatMap(isActive) != nil {
                        return true
                    } else {
                        return false
                    }
                },
                set: { newValue, transaction in
                    if !newValue {
                        selection = nil
                    }
                }
            ),
            label: { EmptyView() }
        )
        // Required to keep the link "active" even when a child link is activated.
        .isDetailLink(false)
    }
}
