package co.touchlab.droidcon.android

import android.content.Intent
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.R
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.service.AndroidNotificationService
import co.touchlab.droidcon.ui.theme.Colors
import co.touchlab.droidcon.ui.util.MainView
import co.touchlab.droidcon.util.AppChecker
import co.touchlab.droidcon.util.NavigationController
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import co.touchlab.kermit.Logger
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

        applicationViewModel.lifecycle.removeFromParent()
        root.addChild(applicationViewModel.lifecycle)

        lifecycleScope.launchWhenCreated {
            syncService.runSynchronization()
        }

        handleNotificationDeeplink(intent)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MainView(viewModel = applicationViewModel)

            val showSplashScreen by applicationViewModel.showSplashScreen.collectAsState()
            Crossfade(targetState = showSplashScreen) { shouldShowSplashScreen ->
                if (shouldShowSplashScreen) {
                    LaunchedEffect(applicationViewModel) {
                        delay(1_000)
                        applicationViewModel.showSplashScreen.value = false
                    }
                    Box(
                        modifier = Modifier
                            .background(Colors.primary)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_splash_screen),
                            contentDescription = getString(R.string.droidcon_title),
                            modifier = Modifier.padding(32.dp)
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
            }
        )
    }

    override fun onResume() {
        super.onResume()
        applicationViewModel.onAppear()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Workaround for a crash we could not reproduce: https://console.firebase.google.com/project/droidcon-148cc/crashlytics/app/android:co.touchlab.droidcon.london/issues/8c559569e69164d7109bd6b1be99ade5
        if (root.hasChild(applicationViewModel.lifecycle)) {
            root.removeChild(applicationViewModel.lifecycle)
        }
    }

    override fun onBackPressed() {
        if (!NavigationController.root.handleBackPress()) {
            super.onBackPressed()
        }
    }
}
