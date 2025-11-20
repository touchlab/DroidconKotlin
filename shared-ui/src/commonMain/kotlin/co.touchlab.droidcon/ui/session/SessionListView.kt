package co.touchlab.droidcon.ui.session

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import co.touchlab.droidcon.ui.theme.Dimensions
import androidx.compose.runtime.collectAsState
import co.touchlab.droidcon.util.NavigationStack
import co.touchlab.droidcon.viewmodel.session.BaseSessionListViewModel
import co.touchlab.droidcon.viewmodel.session.SessionDayViewModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun SessionListView(viewModel: BaseSessionListViewModel, title: String, emptyText: String) {
    NavigationStack(
        key = viewModel,
        links = {
            navigationLink(viewModel.observePresentedSessionDetail) {
                SessionDetailView(viewModel = it)
            }
        },
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(title) },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { innerPadding ->
            var size by remember { mutableStateOf(IntSize(0, 0)) }
            Column(
                modifier = Modifier
                    .onSizeChanged { size = it }
                    .padding(top = innerPadding.calculateTopPadding()),
            ) {
                val days by viewModel.observeDays.collectAsState()
                if (days?.isEmpty() != false) {
                    EmptyView(emptyText)
                } else {
                    val selectedDay by viewModel.observeSelectedDay.collectAsState()
                    val selectedTabIndex = viewModel.days?.indexOf(selectedDay) ?: 0
                    val coroutineScope = rememberCoroutineScope()

                    val pagerState = rememberPagerState(
                        initialPage = selectedTabIndex,
                        pageCount = {
                            days?.size ?: 0
                        },
                    )

                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            if (tabPositions.indices.contains(pagerState.currentPage)) {
                                TabIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                )
                            } else {
                                Logger.w(
                                    "SessionList TabRow requested an indicator for selectedTabIndex: " +
                                        "${pagerState.currentPage}, but only got ${tabPositions.count()} tabs.",
                                )
                                TabRowDefaults.SecondaryIndicator()
                            }
                        },
                    ) {
                        days?.forEachIndexed { index, daySchedule ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    viewModel.selectedDay = daySchedule

                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ) {
                                Text(
                                    text = daySchedule.day,
                                    modifier = Modifier.padding(Dimensions.Padding.default),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }

                    LaunchedEffect(pagerState) {
                        snapshotFlow { pagerState.currentPage }.collect { page ->
                            viewModel.selectedDay = viewModel.days?.get(page)
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                    ) { page ->
                        val day = days?.get(page) ?: return@HorizontalPager
                        val scrollState = rememberLazyListState(
                            day.scrollState.firstVisibleItemIndex,
                            day.scrollState.firstVisibleItemScrollOffset,
                        )
                        if (
                            day.scrollState.firstVisibleItemIndex != scrollState.firstVisibleItemIndex ||
                            day.scrollState.firstVisibleItemScrollOffset != scrollState.firstVisibleItemScrollOffset
                        ) {
                            day.scrollState = SessionDayViewModel.ScrollState(
                                scrollState.firstVisibleItemIndex,
                                scrollState.firstVisibleItemScrollOffset,
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

@Composable
private fun TabIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 64.dp)
            .height(2.dp)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(MaterialTheme.colorScheme.primary),
    )
}

@Composable
private fun EmptyView(text: String) {
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
            text = text,
            modifier = Modifier.padding(Dimensions.Padding.default),
            textAlign = TextAlign.Center,
        )
    }
}
