package co.touchlab.droidcon.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ui.icons.DateRange
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.util.NavigationStack
import co.touchlab.droidcon.viewmodel.session.BaseSessionListViewModel
import co.touchlab.droidcon.viewmodel.session.ScheduleViewModel
import co.touchlab.droidcon.viewmodel.session.SessionDayViewModel
import co.touchlab.kermit.Logger

@Composable
internal fun SessionListView(viewModel: BaseSessionListViewModel) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    NavigationStack(key = viewModel, links = {
        NavigationLink(viewModel.observePresentedSessionDetail) {
            SessionDetailView(viewModel = it)
        }
    }) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Droidcon Berlin 2022") },
                    elevation = 0.dp,
                    modifier = Modifier.shadow(AppBarDefaults.TopAppBarElevation),
                )
            },
        ) {
            Column {
                val days by viewModel.observeDays.observeAsState()
                if (days?.isEmpty() != false) {
                    EmptyView()
                } else {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        indicator = { tabPositions ->
                            if (tabPositions.indices.contains(selectedTabIndex)) {
                                TabRowDefaults.Indicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                                )
                            } else {
                                Logger.w("SessionList TabRow requested an indicator for selectedTabIndex: $selectedTabIndex, but only got ${tabPositions.count()} tabs.")
                                TabRowDefaults.Indicator()
                            }
                        }
                    ) {
                        days?.forEachIndexed { index, daySchedule ->
                            Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }) {
                                Text(
                                    text = daySchedule.day,
                                    modifier = Modifier.padding(Dimensions.Padding.default),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                    days?.forEachIndexed { index, day ->
                        val state =
                            rememberLazyListState(day.scrollState.firstVisibleItemIndex, day.scrollState.firstVisibleItemScrollOffset)
                        if (
                            day.scrollState.firstVisibleItemIndex != state.firstVisibleItemIndex ||
                            day.scrollState.firstVisibleItemScrollOffset != state.firstVisibleItemScrollOffset
                        ) {
                            day.scrollState =
                                SessionDayViewModel.ScrollState(state.firstVisibleItemIndex, state.firstVisibleItemScrollOffset)
                        }

                        if (index == selectedTabIndex) {
                            LazyColumn(state = state, contentPadding = PaddingValues(vertical = Dimensions.Padding.quarter)) {
                                items(day.blocks) { hourBlock ->
                                    Box(
                                        modifier = Modifier.padding(
                                            vertical = Dimensions.Padding.quarter,
                                            horizontal = Dimensions.Padding.half,
                                        ),
                                    ) {
                                        SessionBlockView(hourBlock)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(Dimensions.Padding.default),
            tint = Color.Yellow,
        )

        Text(
            text = "Sessions could not be loaded.",
            modifier = Modifier.padding(Dimensions.Padding.default),
            textAlign = TextAlign.Center,
        )
    }
}
