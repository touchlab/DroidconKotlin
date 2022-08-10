package co.touchlab.droidcon.ios.viewmodel.session

import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.FeedbackService
import co.touchlab.droidcon.ios.util.formatter.DateFormatter
import co.touchlab.droidcon.ios.viewmodel.FeedbackDialogViewModel
import co.touchlab.droidcon.ios.viewmodel.settings.WebLink
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Instant
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import org.brightify.hyperdrive.multiplatformx.property.asFlow
import org.brightify.hyperdrive.multiplatformx.property.flatMapLatest
import org.brightify.hyperdrive.multiplatformx.property.identityEqualityPolicy
import org.brightify.hyperdrive.multiplatformx.property.map

// TODO: Make all properties observable when API is updated.
class SessionDetailViewModel(
    private val sessionGateway: SessionGateway,
    private val settingsGateway: SettingsGateway,
    private val speakerListItemFactory: SpeakerListItemViewModel.Factory,
    private val speakerDetailFactory: SpeakerDetailViewModel.Factory,
    private val feedbackDialogFactory: FeedbackDialogViewModel.Factory,
    private val dateFormatter: DateFormatter,
    private val dateTimeService: DateTimeService,
    private val feedbackService: FeedbackService,
    initialItem: ScheduleItem,
): BaseViewModel() {
    private val item by collected(initialItem, sessionGateway.observeScheduleItem(initialItem.session.id), identityEqualityPolicy())
    private val observeItem by observe(::item)

    private val time: Instant by collected(dateTimeService.now(), flow {
        while (true) {
            emit(dateTimeService.now())
            delay(10_000)
        }
    })
    private val observeTime by observe(::time)

    val title by observeItem.map { it.session.title }
    val observeTitle by observe(::title)
    val info by observeItem.map {
        listOfNotNull(
            it.room?.name,
            with(dateTimeService) {
                dateFormatter.timeOnlyInterval(
                    it.session.startsAt.toConferenceDateTime(),
                    it.session.endsAt.toConferenceDateTime(),
                )
            },
        ).joinToString()
    }
    val observeInfo by observe(::info)

    val state: SessionState? by observeItem.flatMapLatest { item ->
        observeTime.map { now ->
            when {
                item.session.endsAt < now -> SessionState.Ended
                item.session.startsAt < now -> SessionState.InProgress
                item.isInConflict -> SessionState.InConflict
                else -> null
            }
        }
    }
    val observeState by observe(::state)
    val abstract by observeItem.map { it.session.description }
    val observeAbstract by observe(::abstract)
    val abstractLinks: List<WebLink> by observeItem.map { it.session.description?.let(::parseUrl) ?: emptyList() }
    val observeAbstractLinks by observe(::abstractLinks)

    val speakers: List<SpeakerListItemViewModel> by managedList(
        observeItem.map {
            it.speakers.map { speaker ->
                speakerListItemFactory.create(speaker, selected = {
                    presentedSpeakerDetail = speakerDetailFactory.create(speaker)
                })
            }
        }
    )
    val observeSpeakers by observe(::speakers)

    val isAttending by observeItem.map { it.session.rsvp.isAttending }
    val observeIsAttending by observe(::isAttending)
    val isAttendingLoading by instanceLock.observeIsLocked

    var presentedSpeakerDetail: SpeakerDetailViewModel? by managed(null)
    val observePresentedSpeakerDetail by observe(::presentedSpeakerDetail)

    var presentedFeedback: FeedbackDialogViewModel? by managed(null)

    val feedbackAlreadyWritten by observeItem.map { it.session.feedback != null }
    val showFeedbackOption by collected(
        initialValue = false,
        settingsGateway.settings()
            .map { it.isFeedbackEnabled }
            .combine(observeState.asFlow()) { feedbackEnabled, state ->
                feedbackEnabled && state == SessionState.Ended
            }
    )

    fun attendingTapped() = instanceLock.runExclusively {
        sessionGateway.setAttending(item.session, attending = !isAttending)
    }

    fun writeFeedbackTapped() {
        presentedFeedback = feedbackDialogFactory.create(
            item.session,
            submit = { feedback ->
                feedbackService.submit(item.session, feedback)
                presentedFeedback = null
            },
            closeAndDisable = null,
            skip = {
                feedbackService.skip(item.session)
                presentedFeedback = null
            },
        )
    }

    private fun parseUrl(text: String): List<WebLink> {
        val urlRegex =
            "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)".toRegex()
        return urlRegex.findAll(text).map { WebLink(it.range, it.value) }.toList()
    }

    enum class SessionState {
        InConflict, InProgress, Ended
    }

    class Factory(
        private val sessionGateway: SessionGateway,
        private val settingsGateway: SettingsGateway,
        private val speakerListItemFactory: SpeakerListItemViewModel.Factory,
        private val speakerDetailFactory: SpeakerDetailViewModel.Factory,
        private val feedbackDialogFactory: FeedbackDialogViewModel.Factory,
        private val dateFormatter: DateFormatter,
        private val dateTimeService: DateTimeService,
        private val feedbackService: FeedbackService,
    ) {
        fun create(
            item: ScheduleItem,
        ) = SessionDetailViewModel(
            sessionGateway,
            settingsGateway,
            speakerListItemFactory,
            speakerDetailFactory,
            feedbackDialogFactory,
            dateFormatter,
            dateTimeService,
            feedbackService,
            item,
        )
    }
}
