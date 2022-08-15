import SwiftUI

struct FilledButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .padding()
            .background(Color("NavBar_Background"))
            .foregroundColor(Color("Accent"))
            .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

struct FilledButtonStyle_Previews: PreviewProvider {
    static var previews: some View {
        Button("Test button", action: {})
            .buttonStyle(FilledButtonStyle())
    }
}
