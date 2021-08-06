package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.ios.util.formatter.DateFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import org.brightify.hyperdrive.multiplatformx.property.flatMapLatest
import org.brightify.hyperdrive.multiplatformx.property.identityEqualityPolicy
import org.brightify.hyperdrive.multiplatformx.property.map

// TODO: Make all properties observable when API is updated.
class SessionDetailViewModel(
    private val sessionGateway: SessionGateway,
    private val speakerListItemFactory: SpeakerListItemViewModel.Factory,
    private val speakerDetailFactory: SpeakerDetailViewModel.Factory,
    private val dateFormatter: DateFormatter,
    private val dateTimeService: DateTimeService,
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
    val abstract by observeItem.map { it.session.description }

    val speakers: List<SpeakerListItemViewModel> by managedList(
        observeItem.map {
            it.speakers.map { speaker ->
                speakerListItemFactory.create(speaker, selected = {
                    presentedSpeakerDetail = speakerDetailFactory.create(speaker)
                })
            }
        }
    )

    val isAttending by observeItem.map { it.session.isAttending }
    val isAttendingLoading by instanceLock.observeIsLocked

    var presentedSpeakerDetail: SpeakerDetailViewModel? by managed(null)

    fun attendingTapped() = instanceLock.runExclusively {
        sessionGateway.setAttending(item.session, attending = !isAttending)
    }

    enum class SessionState {
        InConflict, InProgress, Ended
    }

    class Factory(
        private val sessionGateway: SessionGateway,
        private val speakerListItemFactory: SpeakerListItemViewModel.Factory,
        private val speakerDetailFactory: SpeakerDetailViewModel.Factory,
        private val dateFormatter: DateFormatter,
        private val dateTimeService: DateTimeService,
    ) {
        fun create(
            item: ScheduleItem,
        ) = SessionDetailViewModel(
            sessionGateway,
            speakerListItemFactory,
            speakerDetailFactory,
            dateFormatter,
            dateTimeService,
            item,
        )
    }
}