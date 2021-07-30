package co.touchlab.droidcon.android.viewModel.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.service.DateTimeFormatterViewService
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.repository.RoomRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SessionDetailViewModel: ViewModel(), KoinComponent {

    private val sessionGateway by inject<SessionGateway>()
    private val roomRepository by inject<RoomRepository>()
    private val dateTimeFormatter by inject<DateTimeFormatterViewService>()

    private val scheduleItem = MutableStateFlow<ScheduleItem?>(null)

    var id = MutableStateFlow<Session.Id?>(null)

    val title: Flow<String> = scheduleItem.map { it?.session?.title ?: "" }
    val description: Flow<String> = scheduleItem.map { it?.session?.description ?: "" }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val room: Flow<Room> = scheduleItem.flatMapLatest { scheduleItem ->
        scheduleItem?.room?.let {
            roomRepository.observe(it)
        } ?: emptyFlow()
    }

    @OptIn(FlowPreview::class)
    val locationInfo: Flow<String> = room.map { it.name }.flatMapConcat { room ->
        scheduleItem.map { scheduleItem ->
            room + (scheduleItem?.session?.let { ", " + dateTimeFormatter.timeRange(it.startsAt, it.endsAt) } ?: "")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val hasEnded: Flow<Boolean> = scheduleItem.flatMapLatest { scheduleItem ->
        flow {
            while (true) {
                val hasEnded =
                    scheduleItem?.session?.startsAt?.let { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) > it }
                        ?: false
                emit(hasEnded)
                delay(1_000)
            }
        }
    }
    val isAttending: Flow<Boolean> = scheduleItem.map { it?.session?.isAttending ?: false }

    private val now: LocalDateTime
        get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val statusRes: Flow<Int> = scheduleItem.map {
        it?.session?.let { session ->
            when {
                it.isInConflict -> R.string.schedule_event_detail_status_conflict
                session.startsAt > now -> R.string.schedule_event_detail_status_future
                session.endsAt < now -> R.string.schedule_event_detail_status_past
                else -> R.string.schedule_event_detail_status_in_progress
            }
        } ?: R.string.schedule_event_detail_status_future
    }

    val speakers: Flow<List<SpeakerViewModel>> = scheduleItem.map { it?.speakers?.map(::SpeakerViewModel) ?: emptyList() }

    init {
        viewModelScope.launch {
            id.map {
                it?.let { sessionGateway.getScheduleItem(it) }
            }.collect {
                scheduleItem.value = it
            }
        }
    }

    fun toggleIsAttending(value: Boolean) {
        // TODO
    }
}