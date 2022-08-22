package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.decompose.whileStarted
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.toConferenceDateTime
import co.touchlab.droidcon.util.DcDispatchers
import co.touchlab.droidcon.util.formatter.DateFormatter
import co.touchlab.droidcon.util.startOfMinute
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration.Companion.seconds

class SessionDayComponent(
    componentContext: ComponentContext,
    dispatchers: DcDispatchers,
    private val dateFormatter: DateFormatter,
    private val dateTimeService: DateTimeService,
    val date: LocalDate,
    private val showAttendingIndicators: Boolean,
    private val scheduleItems: suspend () -> List<ScheduleItem>,
    private val sessionSelected: (Session.Id) -> Unit,
): ComponentContext by componentContext {

    private val _model = MutableValue(Model())
    val model: Value<Model> get() = _model

    init {
        whileStarted(dispatchers.main) {
            _model.reduce { it.copy(blocks = loadBlocks()) }
        }

        whileStarted(dispatchers.main) {
            while (coroutineContext.isActive) {
                delay(10.seconds)
                updateTimes()
            }
        }
    }

    private suspend fun loadBlocks(): List<Model.Block> =
        scheduleItems()
            .filter { it.session.startsAt.toConferenceDateTime(dateTimeService).date == date }
            .groupBy { it.session.startsAt.toConferenceDateTime(dateTimeService).startOfMinute }
            .map { (startsAt, items) -> block(startsAt, items) }

    private fun block(startsAt: LocalDateTime, items: List<ScheduleItem>): Model.Block =
        Model.Block(
            time = dateFormatter.timeOnly(startsAt) ?: "",
            items = items.map(::item),
        )

    private fun item(item: ScheduleItem): Model.Item =
        Model.Item(
            id = item.session.id,
            title = item.session.title,
            isServiceSession = item.session.isServiceSession,
            isAttending = showAttendingIndicators && item.session.rsvp.isAttending,
            isInConflict = item.isInConflict,
            speakers = item.speakers.joinToString { it.fullName },
            room = item.room?.name,
            isInPast = dateTimeService.now() > item.session.endsAt,
            endsAt = item.session.endsAt,
        )

    private fun updateTimes() {
        _model.reduce { model ->
            model.copy(
                blocks = model.blocks.map { block ->
                    block.copy(
                        items = block.items.map { item ->
                            item.copy(isInPast = dateTimeService.now() > item.endsAt)
                        }
                    )
                }
            )
        }
    }

    fun itemSelected(item: Model.Item) {
        if (!item.isServiceSession) {
            sessionSelected(item.id)
        }
    }

    data class Model(
        val blocks: List<Block> = emptyList(),
    ) {

        data class Block(
            val time: String,
            val items: List<Item>,
        )

        data class Item(
            val id: Session.Id,
            val title: String,
            val isServiceSession: Boolean,
            val isAttending: Boolean,
            val isInConflict: Boolean,
            val speakers: String,
            val room: String?,
            val isInPast: Boolean,
            val endsAt: Instant,
        )
    }

    class Factory(
        private val dispatchers: DcDispatchers,
        private val dateFormatter: DateFormatter,
        private val dateTimeService: DateTimeService,
    ) {

        fun create(
            componentContext: ComponentContext,
            date: LocalDate,
            showAttendingIndicators: Boolean,
            scheduleItems: suspend () -> List<ScheduleItem>,
            sessionSelected: (Session.Id) -> Unit,
        ): SessionDayComponent =
            SessionDayComponent(
                componentContext,
                dispatchers,
                dateFormatter,
                dateTimeService,
                date,
                showAttendingIndicators,
                scheduleItems,
                sessionSelected,
            )
    }
}
