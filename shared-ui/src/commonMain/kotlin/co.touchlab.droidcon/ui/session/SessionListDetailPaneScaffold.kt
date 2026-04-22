package co.touchlab.droidcon.ui.session

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.viewmodel.session.BaseSessionListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                    )
                } else {
                    Column {
                        Text("TODO")
                    }
                }
            }
        },
    )
}
