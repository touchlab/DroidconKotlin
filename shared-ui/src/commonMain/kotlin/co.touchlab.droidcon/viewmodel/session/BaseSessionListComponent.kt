package co.touchlab.droidcon.viewmodel.session

import co.touchlab.droidcon.decompose.whileStarted
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.LocalDateParceler
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.toConferenceDateTime
import co.touchlab.droidcon.util.DcDispatchers
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.parcelable.WriteWith
import kotlinx.datetime.LocalDate

abstract class BaseSessionListComponent(
    componentContext: ComponentContext,
    dispatchers: DcDispatchers,
    private val showAttendingIndicators: Boolean,
    private val scheduleItems: suspend () -> List<ScheduleItem>,
    private val sessionDaysFactory: SessionDaysComponent.Factory,
    private val dateTimeService: DateTimeService,
    private val sessionSelected: (Session.Id) -> Unit,
): ComponentContext by componentContext {

    private val navigation = StackNavigation<ChildConfig>()
    private val _stack = childStack(source = navigation, initialConfiguration = ChildConfig.Loading, childFactory = ::child)
    val stack: Value<ChildStack<*, Child>> get() = _stack

    init {
        whileStarted(dispatchers.main) {
            val days = scheduleItems().map { it.session.startsAt.toConferenceDateTime(dateTimeService).date }.distinct()
            navigation.replaceCurrent(if (days.isNotEmpty()) ChildConfig.Days(days) else ChildConfig.Empty)
        }
    }

    private fun child(config: ChildConfig, componentContext: ComponentContext): Child =
        when (config) {
            is ChildConfig.Loading -> Child.Loading

            is ChildConfig.Days ->
                Child.Days(
                    sessionDaysFactory.create(
                        componentContext = componentContext,
                        days = config.days,
                        showAttendingIndicators = showAttendingIndicators,
                        scheduleItems = scheduleItems,
                        sessionSelected = sessionSelected,
                    )
                )

            is ChildConfig.Empty -> Child.Empty
        }

    sealed interface Child {
        object Loading: Child
        class Days(val component: SessionDaysComponent): Child
        object Empty: Child
    }

    private sealed interface ChildConfig: Parcelable {
        @Parcelize
        object Loading: ChildConfig

        @Parcelize
        data class Days(val days: List<@WriteWith<LocalDateParceler> LocalDate>): ChildConfig

        @Parcelize
        object Empty: ChildConfig
    }
}
