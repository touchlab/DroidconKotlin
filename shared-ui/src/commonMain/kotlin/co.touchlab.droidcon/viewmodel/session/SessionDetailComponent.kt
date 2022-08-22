package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.application.composite.Settings
import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.decompose.interfaceLock
import co.touchlab.droidcon.decompose.whileStarted
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.dto.WebLink
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.util.DcDispatchers
import co.touchlab.droidcon.util.formatter.DateFormatter
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Instant

// TODO: Make all properties observable when API is updated.
class SessionDetailComponent(
    componentContext: ComponentContext,
    dispatchers: DcDispatchers,
    private val sessionGateway: SessionGateway,
    settingsGateway: SettingsGateway,
    private val dateFormatter: DateFormatter,
    private val dateTimeService: DateTimeService,
    private val parseUrlViewService: ParseUrlViewService,
    sessionId: Session.Id,
    private val speakerSelected: (Profile) -> Unit,
    private val showFeedback: (Session) -> Unit,
    private val backPressed: () -> Unit,
): ComponentContext by componentContext {

    private val instanceLock = interfaceLock(dispatchers.main)
    private var item: ScheduleItem? = null

    private val _model = MutableValue(Model())
    val model: Value<Model> get() = _model

    private val isAttendingLoading = MutableStateFlow(false)

    init {
        whileStarted(dispatchers.main) {
            combine(
                flow = sessionGateway.observeScheduleItem(sessionId).onEach { item = it },
                flow2 = flow {
                    while (true) {
                        emit(dateTimeService.now())
                        delay(10_000)
                    }
                },
                flow3 = settingsGateway.settings(),
                flow4 = isAttendingLoading,
                transform = ::model
            ).collect {
                _model.value = it
            }
        }

        whileStarted(dispatchers.main) {
            sessionGateway.observeScheduleItem(sessionId)
                .map { item ->
                    item.speakers.map { profile ->
                        Model.Speaker(
                            profile = profile,
                            avatarUrl = profile.profilePicture,
                            info = listOfNotNull(profile.fullName, profile.tagLine).joinToString(),
                            bio = profile.bio,
                        )
                    }
                }
                .collect { speakers ->
                    _model.reduce { it.copy(speakers = speakers) }
                }
        }
    }

    private fun model(
        item: ScheduleItem,
        time: Instant,
        settings: Settings,
        isAttendingLoading: Boolean,
    ): Model {
        val state = sessionState(item, time)

        return Model(
            title = item.session.title,
            info = sessionInfo(item),
            state = state,
            abstract = item.session.description,
            abstractLinks = item.session.description?.let(parseUrlViewService::parse) ?: emptyList(),
            isAttending = item.session.rsvp.isAttending,
            isAttendingLoading = isAttendingLoading,
            feedbackAlreadyWritten = item.session.feedback != null,
            showFeedbackOption = settings.isFeedbackEnabled && state == SessionState.Ended,
        )
    }

    private fun sessionInfo(item: ScheduleItem): String =
        listOfNotNull(
            item.room?.name,
            with(dateTimeService) {
                dateFormatter.timeOnlyInterval(
                    item.session.startsAt.toConferenceDateTime(),
                    item.session.endsAt.toConferenceDateTime(),
                )
            },
        ).joinToString()

    private fun sessionState(item: ScheduleItem, time: Instant): SessionState =
        when {
            item.session.endsAt < time -> SessionState.Ended
            item.session.startsAt < time -> SessionState.InProgress
            item.isInConflict -> SessionState.InConflict
            else -> SessionState.None
        }

    fun attendingTapped() {
        item?.session?.also { session ->
            instanceLock.runExclusively {
                isAttendingLoading.value = true
                sessionGateway.setAttending(session, attending = !model.value.isAttending)
                isAttendingLoading.value = false
            }
        }
    }

    fun writeFeedbackTapped() {
        item?.session?.also(showFeedback)
    }

    fun backTapped() {
        backPressed()
    }

    fun speakerTapped(speaker: Model.Speaker) {
        speakerSelected(speaker.profile)
    }

    data class Model(
        val title: String = "",
        val info: String = "",
        val state: SessionState = SessionState.None,
        val abstract: String? = null,
        val abstractLinks: List<WebLink> = emptyList(),
        val isAttending: Boolean = false,
        val isAttendingLoading: Boolean = false,
        val feedbackAlreadyWritten: Boolean = false,
        val showFeedbackOption: Boolean = false,
        val speakers: List<Speaker> = emptyList(),
    ) {

        data class Speaker(
            val profile: Profile,
            val avatarUrl: Url?,
            val info: String,
            val bio: String?,
        )
    }

    enum class SessionState {
        InConflict, InProgress, Ended, None
    }

    class Factory(
        private val dispatchers: DcDispatchers,
        private val sessionGateway: SessionGateway,
        private val settingsGateway: SettingsGateway,
        private val dateFormatter: DateFormatter,
        private val dateTimeService: DateTimeService,
        private val parseUrlViewService: ParseUrlViewService,
    ) {

        fun create(
            componentContext: ComponentContext,
            sessionId: Session.Id,
            speakerSelected: (Profile) -> Unit,
            showFeedback: (Session) -> Unit,
            backPressed: () -> Unit,
        ) = SessionDetailComponent(
            componentContext,
            dispatchers,
            sessionGateway,
            settingsGateway,
            dateFormatter,
            dateTimeService,
            parseUrlViewService,
            sessionId,
            speakerSelected,
            showFeedback,
            backPressed,
        )
    }
}
