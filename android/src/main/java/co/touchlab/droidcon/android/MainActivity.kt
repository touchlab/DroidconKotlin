package co.touchlab.droidcon.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.R
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.util.AppChecker
import co.touchlab.droidcon.service.AndroidNotificationService
import co.touchlab.droidcon.ui.theme.Colors
import co.touchlab.droidcon.ui.util.MainView
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import co.touchlab.droidcon.util.NavigationController
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import org.brightify.hyperdrive.multiplatformx.LifecycleGraph
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity: ComponentActivity(), KoinComponent {

    private val notificationSchedulingService: NotificationSchedulingService by inject()
    private val syncService: SyncService by inject()
    private val analyticsService: AnalyticsService by inject()

    private val applicationViewModel: ApplicationViewModel by inject()

    private val root = LifecycleGraph.Root(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        AppChecker.checkTimeZoneHash()

        analyticsService.logEvent(AnalyticsService.EVENT_STARTED)

        lifecycleScope.launchWhenCreated {
            notificationSchedulingService.runScheduling()
        }

        lifecycleScope.launchWhenCreated {
            syncService.runSynchronization()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        root.addChild(applicationViewModel.lifecycle)

        setContent {
            Box(modifier = Modifier.background(Colors.primary)) {
                ProvideWindowInsets {
                    MainView(viewModel = applicationViewModel)
                }
            }

            var showSplashScreen by remember(applicationViewModel) { mutableStateOf(true) }
            Crossfade(targetState = showSplashScreen) { shouldShowSplashScreen ->
                if (shouldShowSplashScreen) {
                    LaunchedEffect(applicationViewModel) {
                        delay(1_000)
                        showSplashScreen = false
                    }
                    Box(
                        modifier = Modifier
                            .background(Colors.primary)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_splash_screen),
                            contentDescription = getString(R.string.droidcon_title),
                            modifier = Modifier.padding(32.dp),
                        )
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            val cancelAttach = root.attach(lifecycleScope)
            try {
                awaitCancellation()
            } finally {
                cancelAttach.cancel()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        applicationViewModel.onAppear()
    }

    override fun onDestroy() {
        super.onDestroy()
        root.removeChild(applicationViewModel.lifecycle)
    }

    override fun onBackPressed() {
        if (!NavigationController.root.handleBackPress()) {
            super.onBackPressed()
        }
    }
}
