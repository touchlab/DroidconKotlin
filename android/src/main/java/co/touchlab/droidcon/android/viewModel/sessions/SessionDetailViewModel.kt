package co.touchlab.droidcon.android.viewModel.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.R
import co.touchlab.droidcon.dto.WebLink
import co.touchlab.droidcon.android.service.DateTimeFormatterViewService
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.android.viewModel.feedback.FeedbackViewModel
import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.FeedbackService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalCoroutinesApi::class)
class SessionDetailViewModel: ViewModel(), KoinComponent {

    private val sessionGateway by inject<SessionGateway>()
    private val dateTimeFormatter by inject<DateTimeFormatterViewService>()
    private val dateTimeService by inject<DateTimeService>()
    private val parseUrlViewService by inject<ParseUrlViewService>()
    private val feedbackService: FeedbackService by inject()
    private val settingsGateway: SettingsGateway by inject()

    private val observeScheduleItem = MutableSharedFlow<ScheduleItem?>(replay = 1)
    private var scheduleItem: ScheduleItem? = null
        set(value) {
            field = value
            observeScheduleItem.tryEmit(value)
        }

    var id = MutableStateFlow<Session.Id?>(null)

    val title: Flow<String> = observeScheduleItem.map { it?.session?.title ?: "" }
    val description: Flow<Pair<String, List<WebLink>>> = observeScheduleItem.map { scheduleItem ->
        scheduleItem?.session?.description?.let { it to parseUrlViewService.parse(it) } ?: "" to emptyList()
    }

    val locationInfo: Flow<String> = observeScheduleItem.map {
        listOfNotNull(
            it?.room?.name,
            it?.let { item ->
                dateTimeFormatter.timeRange(
                    item.session.startsAt,
                    item.session.endsAt,
                )
            },
        ).joinToString()
    }

    val hasEnded: Flow<Boolean> = observeScheduleItem.flatMapLatest { scheduleItem ->
        flow {
            while (currentCoroutineContext().isActive) {
                val hasEnded = scheduleItem?.session?.endsAt?.let { dateTimeService.now() > it } ?: false
                emit(hasEnded)
                delay(1_000)
            }
        }
    }

    val isAttending: Flow<Boolean> = observeScheduleItem.map { it?.session?.rsvp?.isAttending ?: false }

    val statusRes: Flow<Int> = observeScheduleItem.mapNotNull {
        if (it == null) {
            return@mapNotNull R.string.schedule_session_detail_status_future
        }
        dateTimeService.now().let { now ->
            when {
                it.session.endsAt < now -> R.string.schedule_session_detail_status_past
                it.session.startsAt <= now && now <= it.session.endsAt -> R.string.schedule_session_detail_status_in_progress
                it.isInConflict -> R.string.schedule_session_detail_status_conflict
                it.session.startsAt > now -> R.string.schedule_session_detail_status_future
                else -> null
            }
        }
    }

    val speakers: Flow<List<ProfileViewModel>> = observeScheduleItem.map { it?.speakers?.map(::ProfileViewModel) ?: emptyList() }

    val showFeedbackOption: StateFlow<Boolean> = settingsGateway.settings()
        .map { it.isFeedbackEnabled }
        .combine(hasEnded) { feedbackEnabled, hasEnded ->
            feedbackEnabled && hasEnded
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val showFeedbackTitleRes: StateFlow<Int> = observeScheduleItem.map {
        if (it?.session?.feedback != null) {
            R.string.feedback_change_title
        } else {
            R.string.feedback_add_title
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = R.string.feedback_add_title)

    val showFeedback: MutableStateFlow<FeedbackViewModel?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
            id.flatMapLatest {
                it?.let { sessionGateway.observeScheduleItem(it) } ?: flowOf(null)
            }.collect {
                scheduleItem = it
            }
        }
    }

    fun toggleIsAttending(value: Boolean) {
        scheduleItem?.session?.let { session ->
            viewModelScope.launch {
                sessionGateway.setAttending(session, value)
            }
        }
    }

    fun writeFeedbackTapped() {
        val session = scheduleItem?.session ?: return
        showFeedback.value = FeedbackViewModel(
            MutableStateFlow(session),
            submitFeedback = { currentSession, feedback ->
                feedbackService.submit(currentSession, feedback)
                showFeedback.value = null
            },
            closeAndDisableFeedback = null,
            skipFeedback = {
                feedbackService.skip(it)
                showFeedback.value = null
            },
        )
    }
}
