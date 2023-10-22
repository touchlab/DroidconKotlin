package co.touchlab.droidcon.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SessionListView(viewModel: BaseSessionListViewModel) {
    NavigationStack(
        key = viewModel,
        links = {
            NavigationLink(viewModel.observePresentedSessionDetail) {
                SessionDetailView(viewModel = it)
            }
        }
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Droidcon London 2023") },
                    scrollBehavior = scrollBehavior
                )
            },
        ) { paddingValues ->
            var size by remember { mutableStateOf(IntSize(0, 0)) }
            Column(modifier = Modifier
                .onSizeChanged { size = it }
                .padding(top = paddingValues.calculateTopPadding())
            ) {
                val days by viewModel.observeDays.observeAsState()
                if (days?.isEmpty() != false) {
                    EmptyView()
                } else {
                    val selectedDay by viewModel.observeSelectedDay.observeAsState()
                    val selectedTabIndex = viewModel.days?.indexOf(selectedDay) ?: 0
                    val state = rememberLazyListState(initialFirstVisibleItemIndex = selectedTabIndex)
                    val coroutineScope = rememberCoroutineScope()

                    if (state.firstVisibleItemIndex != selectedTabIndex && !state.isScrollInProgress) {
                        coroutineScope.launch {
                            state.scrollToItem(selectedTabIndex)
                        }
                    }

                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        indicator = { tabPositions ->
                            if (tabPositions.indices.contains(selectedTabIndex)) {
                                TabIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                                )
                            } else {
                                Logger.w("SessionList TabRow requested an indicator for selectedTabIndex: $selectedTabIndex, but only got ${tabPositions.count()} tabs.")
                                TabRowDefaults.Indicator()
                            }
                        }
                    ) {
                        days?.forEachIndexed { index, daySchedule ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = {
                                    viewModel.selectedDay = daySchedule

                                    coroutineScope.launch {
                                        state.animateScrollToItem(index)
                                    }
                                },
                                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ) {
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
                        viewModel.selectedDay = viewModel.days?.get(scrollToIndex)
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
private fun TabIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(MaterialTheme.colorScheme.primary)
    )
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
            tint = MaterialTheme.colorScheme.secondary,
        )

        Text(
            text = "Sessions could not be loaded.",
            modifier = Modifier.padding(Dimensions.Padding.default),
            textAlign = TextAlign.Center,
        )
    }
}
