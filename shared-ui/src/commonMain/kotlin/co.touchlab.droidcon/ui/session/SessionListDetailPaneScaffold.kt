package co.touchlab.droidcon.ui.session

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.viewmodel.session.BaseSessionListViewModel
import droidcon.shared_ui.generated.resources.Res
import droidcon.shared_ui.generated.resources.event_note
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SessionListDetailPaneScaffold(viewModel: BaseSessionListViewModel, title: String, emptyText: String, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    val presentedSessionDetail by viewModel.observePresentedSessionDetail.observeAsState()
    val listPaneAdapted = navigator.scaffoldValue[ListDetailPaneScaffoldRole.List]
    val fullWidth = listPaneAdapted == PaneAdaptedValue.Hidden
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isCompactWidth = !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val usePaneSlideAnimation = fullWidth && isCompactWidth
    val listEnterTransition = if (usePaneSlideAnimation) slideInHorizontally { -it } else EnterTransition.None
    val listExitTransition = if (usePaneSlideAnimation) slideOutHorizontally { -it } else ExitTransition.None
    val detailEnterTransition = if (usePaneSlideAnimation) slideInHorizontally { it } else EnterTransition.None
    val detailExitTransition = if (usePaneSlideAnimation) slideOutHorizontally { it } else ExitTransition.None

    ListDetailPaneScaffold(
        modifier = modifier,
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane(
                enterTransition = listEnterTransition,
                exitTransition = listExitTransition,
            ) {
                SessionListView(
                    viewModel = viewModel,
                    title = title,
                    emptyText = emptyText,
                ) {
                    scope.launch {
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane(
                enterTransition = detailEnterTransition,
                exitTransition = detailExitTransition,
            ) {
                val detailViewModel = presentedSessionDetail
                if (detailViewModel != null) {
                    SessionDetailView(
                        viewModel = detailViewModel,
                        showBackButton = fullWidth,
                        onBack = {
                            scope.launch {
                                navigator.navigateBack()
                                if (!fullWidth) {
                                    delay(1000)
                                }
                                viewModel.presentedSessionDetail = null
                            }
                        },
                        attendingTapped = { attending ->
                            if (!attending) {
                                scope.launch {
                                    viewModel.presentedSessionDetail = null
                                    navigator.navigateBack()
                                    if (!fullWidth) {
                                        delay(1000)
                                    }
                                }
                            }
                        },
                    )
                } else {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("No session selected. Please select a session for more details", style = MaterialTheme.typography.titleLarge)
                        Icon(modifier = Modifier.fillMaxSize(0.33f), painter = painterResource(Res.drawable.event_note), contentDescription = "No Session Selected")
                    }
                }
            }
        },
    )
}
