package co.touchlab.droidcon.ui.session

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ui.icons.DateRange
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.util.NavigationStack
import co.touchlab.droidcon.viewmodel.session.BaseSessionListViewModel
import co.touchlab.droidcon.viewmodel.session.SessionDayViewModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch

@Composable
internal fun SessionListView(viewModel: BaseSessionListViewModel) {
    NavigationStack(key = viewModel, links = {
        NavigationLink(viewModel.observePresentedSessionDetail) {
            SessionDetailView(viewModel = it)
        }
    }) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Droidcon London 2022") },
                    elevation = 0.dp,
                    modifier = Modifier.shadow(AppBarDefaults.TopAppBarElevation),
                    backgroundColor = MaterialTheme.colors.primary,
                )
            },
        ) {
            var size by remember { mutableStateOf(IntSize(0, 0)) }
            Column(modifier = Modifier.onSizeChanged { size = it }) {
                val days by viewModel.observeDays.observeAsState()
                if (days?.isEmpty() != false) {
                    EmptyView()
                } else {
                    val selectedTabIndex by viewModel.observeSelectedDayIndex.observeAsState()
                    val state = rememberLazyListState(initialFirstVisibleItemIndex = viewModel.selectedDayIndex)
                    val coroutineScope = rememberCoroutineScope()

                    if (state.firstVisibleItemIndex != selectedTabIndex && !state.isScrollInProgress) {
                        coroutineScope.launch {
                            state.scrollToItem(selectedTabIndex)
                        }
                    }

                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        backgroundColor = MaterialTheme.colors.primary,
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
                            Tab(selected = selectedTabIndex == index, onClick = {
                                viewModel.selectedDayIndex = index

                                coroutineScope.launch {
                                    state.animateScrollToItem(index)
                                }
                            }) {
                                Text(
                                    text = daySchedule.day,
                                    modifier = Modifier.padding(Dimensions.Padding.default),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }

                    val interaction by state.interactionSource.interactions.collectAsState(null)
                    if (interaction is DragInteraction.Stop) {
                        val scrollToIndex = if (state.firstVisibleItemScrollOffset >= size.width / 2) {
                            state.firstVisibleItemIndex + 1
                        } else {
                            state.firstVisibleItemIndex
                        }
                        LaunchedEffect(interaction) {
                            state.animateScrollToItem(scrollToIndex)
                        }
                        viewModel.selectedDayIndex = scrollToIndex
                    }

                    LazyRow(state = state) {
                        items(days ?: emptyList()) { day ->
                            val scrollState =
                                rememberLazyListState(day.scrollState.firstVisibleItemIndex, day.scrollState.firstVisibleItemScrollOffset)
                            if (
                                day.scrollState.firstVisibleItemIndex != scrollState.firstVisibleItemIndex ||
                                day.scrollState.firstVisibleItemScrollOffset != scrollState.firstVisibleItemScrollOffset
                            ) {
                                day.scrollState = SessionDayViewModel.ScrollState(
                                    scrollState.firstVisibleItemIndex,
                                    scrollState.firstVisibleItemScrollOffset
                                )
                            }

                            val density = LocalDensity.current
                            LazyColumn(
                                state = scrollState,
                                contentPadding = PaddingValues(vertical = Dimensions.Padding.quarter),
                                modifier = Modifier.width(with(density) { size.width.toDp() }),
                            ) {
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
            tint = MaterialTheme.colors.secondary,
        )

        Text(
            text = "Sessions could not be loaded.",
            modifier = Modifier.padding(Dimensions.Padding.default),
            textAlign = TextAlign.Center,
        )
    }
}
