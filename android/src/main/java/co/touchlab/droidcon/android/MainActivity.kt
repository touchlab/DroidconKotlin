package co.touchlab.droidcon.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
// import androidx.compose.animation.Crossfade
// import androidx.compose.foundation.Image
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.foundation.layout.padding
// import androidx.compose.material.MaterialTheme
// import androidx.compose.material.Surface
// import androidx.compose.runtime.LaunchedEffect
// import androidx.compose.runtime.collectAsState
// import androidx.compose.runtime.getValue
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.res.painterResource
// import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.viewModel.MainViewModel
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.service.AndroidNotificationService
import co.touchlab.droidcon.util.AppChecker
import co.touchlab.droidcon.ui.util.MainView
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import co.touchlab.droidcon.util.NavigationController
import co.touchlab.kermit.Logger
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.awaitCancellation
import org.brightify.hyperdrive.multiplatformx.LifecycleGraph
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity: ComponentActivity(), KoinComponent {

    private val notificationSchedulingService: NotificationSchedulingService by inject()
    private val syncService: SyncService by inject()
    private val analyticsService: AnalyticsService by inject()
    private val mainViewModel: MainViewModel by viewModels()

    private val applicationViewModel: ApplicationViewModel by inject()

    private val root = LifecycleGraph.Root(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        AppChecker.checkTimeZoneHash()

        analyticsService.logEvent(AnalyticsService.EVENT_STARTED)

        if (applicationViewModel.lifecycle.isAttached) {
            applicationViewModel.lifecycle.removeFromParent()
        }
        root.addChild(applicationViewModel.lifecycle)

        lifecycleScope.launchWhenCreated {
            notificationSchedulingService.runScheduling()
        }

        lifecycleScope.launchWhenCreated {
            syncService.runSynchronization()
        }

        handleNotificationDeeplink(intent)

        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            Box(modifier = Modifier.background(Colors.primary)) {
                ProvideWindowInsets {
                    MainView(viewModel = applicationViewModel)
                }
            }

            // DroidconTheme {
            //     ProvideWindowInsets {
            //         Surface(color = MaterialTheme.colors.primary, modifier = Modifier.fillMaxSize()) {
            //             Main(main = mainViewModel)
            //         }
            //
            //         val showSplashScreen by mainViewModel.showSplashScreen.collectAsState()
            //         Crossfade(targetState = showSplashScreen) { shouldShowSplashScreen ->
            //             if (shouldShowSplashScreen) {
            //                 LaunchedEffect(mainViewModel) {
            //                     mainViewModel.didShowSplashScreen()
            //                 }
            //                 Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
            //                     Image(
            //                         painter = painterResource(id = R.drawable.ic_splash_screen),
            //                         contentDescription = getString(R.string.droidcon_title),
            //                         modifier = Modifier
            //                             .padding(32.dp)
            //                             .fillMaxSize(0.75f),
            //                     )
            //                 }
            //             }
            //         }
            //     }

            // }
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.let(::handleNotificationDeeplink)
    }

    private fun handleNotificationDeeplink(intent: Intent) {
        val type = intent.getStringExtra(AndroidNotificationService.NOTIFICATION_TYPE_EXTRA_KEY) ?: return
        val sessionId = intent.getStringExtra(AndroidNotificationService.NOTIFICATION_SESSION_ID_EXTRA_KEY) ?: return
        applicationViewModel.notificationReceived(
            sessionId,
            when (type) {
                AndroidNotificationService.NOTIFICATION_TYPE_EXTRA_REMINDER -> NotificationService.NotificationType.Reminder
                AndroidNotificationService.NOTIFICATION_TYPE_EXTRA_FEEDBACK -> NotificationService.NotificationType.Feedback
                else -> {
                    Logger.w("Unknown notification type $type.")
                    return
                }
            },
        )
    }

    override fun onResume() {
        super.onResume()
        // mainViewModel.initializeFeedbackObserving()
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
