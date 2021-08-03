package co.touchlab.droidcon.android.viewModel.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.dto.WebLink
import co.touchlab.droidcon.android.service.DateTimeFormatterViewService
import co.touchlab.droidcon.android.service.ParseUrlViewService
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.domain.service.DateTimeService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.isActive
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalCoroutinesApi::class)
class SessionDetailViewModel: ViewModel(), KoinComponent {

    private val sessionGateway by inject<SessionGateway>()
    private val dateTimeFormatter by inject<DateTimeFormatterViewService>()
    private val dateTimeService by inject<DateTimeService>()
    private val parseUrlViewService by inject<ParseUrlViewService>()

    private val scheduleItem = MutableStateFlow<ScheduleItem?>(null)

    var id = MutableStateFlow<Session.Id?>(null)

    val title: Flow<String> = scheduleItem.map { it?.session?.title ?: "" }
    val description: Flow<Pair<String, List<WebLink>>> = scheduleItem.map { scheduleItem ->
        scheduleItem?.session?.description?.let { it to parseUrlViewService.parse(it) } ?: "" to emptyList()
    }

    val locationInfo: Flow<String> = scheduleItem.map {
        listOfNotNull(
            it?.room?.name,
            it?.let { item ->
                with(dateTimeService) {
                    dateTimeFormatter.timeRange(
                        item.session.startsAt.toConferenceDateTime(),
                        item.session.endsAt.toConferenceDateTime(),
                    )
                }
            },
        ).joinToString()
    }

    val hasEnded: Flow<Boolean> = scheduleItem.flatMapLatest { scheduleItem ->
        flow {
            while (currentCoroutineContext().isActive) {
                val hasEnded = scheduleItem?.session?.endsAt?.let { dateTimeService.now() > it } ?: false
                emit(hasEnded)
                delay(1_000)
            }
        }
    }
    val isAttending: Flow<Boolean> = scheduleItem.map { it?.session?.isAttending ?: false }

    val statusRes: Flow<Int> = scheduleItem.mapNotNull {
        if (it == null) { return@mapNotNull R.string.schedule_session_detail_status_future }
        dateTimeService.now().let { now ->
            when {
                it.session.endsAt < now -> R.string.schedule_session_detail_status_past
                it.session.startsAt > now -> R.string.schedule_session_detail_status_future
                it.isInConflict -> R.string.schedule_session_detail_status_conflict
                else -> null
            }
        }
    }

    val speakers: Flow<List<SpeakerViewModel>> = scheduleItem.map { it?.speakers?.map(::SpeakerViewModel) ?: emptyList() }

    init {
        viewModelScope.launch {
            id.flatMapLatest {
                it?.let { sessionGateway.observeScheduleItem(it) } ?: flowOf(null)
            }.collect {
                scheduleItem.value = it
            }
        }
    }

    fun toggleIsAttending(value: Boolean) {
        // TODO: Set to gateway.
    }
}