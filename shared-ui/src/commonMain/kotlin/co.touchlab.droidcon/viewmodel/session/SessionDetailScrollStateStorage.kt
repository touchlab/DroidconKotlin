package co.touchlab.droidcon.viewmodel.session

import kotlinx.datetime.LocalDate

class SessionDetailScrollStateStorage {

    val scrollStates: MutableMap<LocalDate, SessionDayViewModel.ScrollState> = mutableMapOf()
    val agendaScrollStates: MutableMap<LocalDate, SessionDayViewModel.ScrollState> = mutableMapOf()

    fun getScrollState(day: LocalDate, agenda: Boolean): SessionDayViewModel.ScrollState =
        if (agenda) {
            agendaScrollStates[day]
        } else {
            scrollStates[day]
        } ?: SessionDayViewModel.ScrollState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0)

    fun setScrollState(day: LocalDate, agenda: Boolean, scrollState: SessionDayViewModel.ScrollState) {
        if (agenda) {
            agendaScrollStates[day] = scrollState
        } else {
            scrollStates[day] = scrollState
        }
    }
}
