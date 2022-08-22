package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.LocalDateParceler
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.util.formatter.DateFormatter
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.parcelable.WriteWith
import kotlinx.datetime.LocalDate

class SessionDaysComponent(
    componentContext: ComponentContext,
    days: List<LocalDate>,
    private val showAttendingIndicators: Boolean,
    dateFormatter: DateFormatter,
    private val scheduleItems: suspend () -> List<ScheduleItem>,
    private val sessionSelected: (Session.Id) -> Unit,
    private val sessionDayFactory: SessionDayComponent.Factory,
): ComponentContext by componentContext {

    private val navigation = StackNavigation<DayConfig>()
    private val _stack = childStack(source = navigation, initialConfiguration = DayConfig(days.first()), childFactory = ::day)
    val stack: Value<ChildStack<*, SessionDayComponent>> get() = _stack

    val days: List<Day> = days.map { Day(date = it, title = dateFormatter.monthWithDay(it) ?: "") }

    private fun day(config: DayConfig, componentContext: ComponentContext): SessionDayComponent =
        sessionDayFactory.create(
            componentContext = componentContext,
            date = config.date,
            showAttendingIndicators = showAttendingIndicators,
            scheduleItems = scheduleItems,
            sessionSelected = sessionSelected,
        )

    fun selectTab(date: LocalDate) {
        navigation.navigate { stack -> stack.filterNot { it.date == date } + DayConfig(date) }
    }

    @Parcelize
    private data class DayConfig(val date: @WriteWith<LocalDateParceler> LocalDate): Parcelable

    data class Day(
        val date: LocalDate,
        val title: String,
    )

    class Factory(
        private val dateFormatter: DateFormatter,
        private val sessionDayFactory: SessionDayComponent.Factory,
    ) {

        fun create(
            componentContext: ComponentContext,
            days: List<LocalDate>,
            showAttendingIndicators: Boolean,
            scheduleItems: suspend () -> List<ScheduleItem>,
            sessionSelected: (Session.Id) -> Unit,
        ): SessionDaysComponent =
            SessionDaysComponent(
                componentContext,
                days,
                showAttendingIndicators,
                dateFormatter,
                scheduleItems,
                sessionSelected,
                sessionDayFactory,
            )
    }
}
