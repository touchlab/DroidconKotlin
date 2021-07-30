import SwiftUI
import DroidconKit

struct DaySelectionView: View {
    @ObservedObject
    private(set) var viewModel: BaseSessionListViewModel

    var body: some View {
        Picker(viewModel.selectedDay?.day ?? "", selection: $viewModel.selectedDay) {
            ForEach(viewModel.days) { day in
                Text(day.day)
                    .tag(day as SessionDayViewModel?)
            }
        }
        .pickerStyle(SegmentedPickerStyle())
    }
}

struct DaySelectionView_Previews: PreviewProvider {
    static var previews: some View {
        EmptyView()
    }
}
