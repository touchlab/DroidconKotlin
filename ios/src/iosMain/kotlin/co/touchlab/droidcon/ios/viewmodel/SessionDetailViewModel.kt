package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.ios.util.formatter.DateFormatter
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import org.brightify.hyperdrive.multiplatformx.InterfaceLock

// TODO: Make all properties observable when API is updated.
class SessionDetailViewModel(
    private val sessionGateway: SessionGateway,
    private val speakerListItemFactory: SpeakerListItemViewModel.Factory,
    private val speakerDetailFactory: SpeakerDetailViewModel.Factory,
    private val dateFormatter: DateFormatter,
    private val dateTimeService: DateTimeService,
    private val timeZone: TimeZone,
    item: ScheduleItem,
): BaseViewModel() {
    val title = item.session.title
    val info = listOfNotNull(
        item.room?.name,
        with(timeZone) {
            dateFormatter.timeOnlyInterval(
                item.session.startsAt.toLocalDateTime(),
                item.session.endsAt.toLocalDateTime(),
            )
       },
    ).joinToString()

    val state: SessionState? = dateTimeService.now().let { now ->
        with(timeZone) {
            when {
                item.session.endsAt.toLocalDateTime() < now -> SessionState.Ended
                item.session.startsAt.toLocalDateTime() < now -> SessionState.InProgress
                item.isInConflict -> SessionState.InConflict
                else -> null
            }
        }
    }
    val abstract = item.session.description

    val speakers: List<SpeakerListItemViewModel> by managedList(
        item.speakers.map {
            speakerListItemFactory.create(it, selected = {
                presentedSpeakerDetail = speakerDetailFactory.create(it)
            })
        }
    )

    var isAttending by published(item.session.isAttending)
        private set
    val isAttendingLoading by collected(instanceLock.observeState) { it == InterfaceLock.State.Running }

    var presentedSpeakerDetail: SpeakerDetailViewModel? by managed(null)

    fun attendingTapped() = instanceLock.runExclusively {
        delay(1_000)
        isAttending = !isAttending
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
        private val timeZone: TimeZone,
    ) {
        fun create(
            item: ScheduleItem,
        ) = SessionDetailViewModel(
            sessionGateway,
            speakerListItemFactory,
            speakerDetailFactory,
            dateFormatter,
            dateTimeService,
            timeZone,
            item,
        )
    }
}