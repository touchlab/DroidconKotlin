package co.touchlab.droidcon.android.ui.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.main.ScheduleScreen
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class DaySchedule(
    val dayString: String,
    val hourBlocks: List<HourBlock>,
)

data class HourBlock(
    val id: Long,
    var time: String,
    val description: String,
)

val days: List<DaySchedule> = listOf(
    DaySchedule(
        "AUG 1",
        listOf(
            HourBlock(0L, "8:00 AM", "Welcome party"),
            HourBlock(1L, "9:00 AM", "First presentation\n\nCome learn something about Compose, it's fun!"),
        ),
    ),
    DaySchedule(
        "AUG 2",
        listOf(
            HourBlock(2L, "8:00 AM", "Welcome party"),
            HourBlock(3L, "11:00 PM", "Last presentation\n\nWe will say our heartfelt goodbyes..."),
        ),
    ),
)

class SessionsDayViewModel(
    date: LocalDate,
    items: List<ScheduleItem>,
) {

    // TODO: Use formatter
    val day: String = date.toString()

    val blocks: List<SessionsBlockViewModel> = items
        .groupBy { it.session.startsAt.startOfMinute }
        .map { (startsAt, items) ->
            SessionsBlockViewModel(startsAt, items)
        }
}

val LocalDateTime.startOfMinute: LocalDateTime
    get() = LocalDateTime(year, month, dayOfMonth, hour, minute)

class SessionsBlockViewModel(
    startsAt: LocalDateTime,
    items: List<ScheduleItem>,
) {

    // TODO: Use formatter
    val time = startsAt.hour.toString()
    val sessions: List<SessionViewModel> = items.map(::SessionViewModel)
}

class SessionViewModel(
    private val item: ScheduleItem,
) {

    val id: Session.Id = item.session.id

    val title: String = item.session.title
    val speakers: String = item.speakers.joinToString(", ") { it.fullName }
}

class ScheduleViewModel: BaseSessionsViewModel(onlyAttending = false)

class AgendaViewModel: BaseSessionsViewModel(onlyAttending = true)

abstract class BaseSessionsViewModel(
    private val onlyAttending: Boolean,
): ViewModel(), KoinComponent {

    val days = MutableStateFlow(emptyList<SessionsDayViewModel>())

    private val sessionGateway by inject<SessionGateway>()
    private val scope = viewModelScope

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        scope.launch {
            val scheduleItems = if (onlyAttending) {
                sessionGateway.getAgenda()
            } else {
                sessionGateway.getSchedule()
            }
            val days = scheduleItems
                .groupBy { it.session.startsAt.date }
                .map { (date, items) ->
                    SessionsDayViewModel(date, items)
                }
            this@BaseSessionsViewModel.days.value = days
        }
    }
}

@Composable
fun Schedule(navController: NavHostController) {
    val schedule = viewModel<ScheduleViewModel>()
    SessionList(navController = navController, sessions = schedule)
}

@Composable
fun SessionList(navController: NavHostController, sessions: BaseSessionsViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val days by sessions.days.collectAsState()
    LaunchedEffect(days) {
        selectedTabIndex = selectedTabIndex.coerceIn(days.indices)
    }

    Scaffold(
        topBar = {
            Toolbar(titleRes = R.string.droidcon_title, navController = navController)
        }
    ) {
        Column {
            TabRow(selectedTabIndex = selectedTabIndex) {
                days.forEachIndexed { index, daySchedule ->
                    Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }) {
                        Text(
                            text = daySchedule.day,
                            modifier = Modifier.padding(Dimensions.Padding.default),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            LazyColumn(contentPadding = PaddingValues(vertical = Dimensions.Padding.quarter)) {
                val daySchedule = days.getOrNull(selectedTabIndex)?.blocks ?: emptyList()
                items(daySchedule) { hourBlock ->
                    Box(modifier = Modifier.padding(vertical = Dimensions.Padding.quarter, horizontal = Dimensions.Padding.half)) {
                        HourBlock(hourBlock) { tappedSession ->
                            navController.navigate(ScheduleScreen.EventDetail.createRoute(tappedSession.id))
                        }
                    }
                }
            }
        }
    }
}