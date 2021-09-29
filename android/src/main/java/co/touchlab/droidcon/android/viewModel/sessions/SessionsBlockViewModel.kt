package co.touchlab.droidcon.android.viewModel.sessions

import androidx.lifecycle.ViewModel
import co.touchlab.droidcon.android.service.DateTimeFormatterViewService
import co.touchlab.droidcon.domain.composite.ScheduleItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SessionsBlockViewModel(
    startsAt: LocalDateTime,
    items: List<ScheduleItem>,
): ViewModel(), KoinComponent {

    private val dateTimeFormatter by inject<DateTimeFormatterViewService>()

    val time: String = dateTimeFormatter.time(startsAt)
    val sessions: List<SessionViewModel> = items.map(::SessionViewModel)

    val hasEnded: Flow<Boolean> = flow {
        while (true) {
            val hasEnded = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) > startsAt
            emit(hasEnded)
            delay(1_000)
        }
    }
}