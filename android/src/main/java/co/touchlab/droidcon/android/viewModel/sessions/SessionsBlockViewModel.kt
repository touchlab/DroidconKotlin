package co.touchlab.droidcon.android.viewModel.sessions

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.android.service.DateTimeFormatterViewService
import co.touchlab.droidcon.domain.composite.ScheduleItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SessionsBlockViewModel(
    startsAt: Instant,
    items: List<ScheduleItem>,
): ViewModel(), KoinComponent {

    private val dateTimeFormatter by inject<DateTimeFormatterViewService>()

    val time: String = dateTimeFormatter.time(startsAt)
    val sessions: List<SessionViewModel> = items.map(::SessionViewModel)

    //TODOKPG - Checking start instant for hasEnded is weird
    val hasEnded: Flow<Boolean> = flow {
        while (true) {
            val hasEnded = Clock.System.now() > startsAt
            emit(hasEnded)
            delay(1_000)
        }
    }
}