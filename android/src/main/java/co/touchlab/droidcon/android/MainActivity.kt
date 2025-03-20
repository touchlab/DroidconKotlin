package co.touchlab.droidcon.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.R
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.service.AndroidNotificationService
import co.touchlab.droidcon.ui.theme.Colors
import co.touchlab.droidcon.ui.util.MainView
import co.touchlab.droidcon.util.NavigationController
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.brightify.hyperdrive.multiplatformx.LifecycleGraph
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity :
    ComponentActivity(),
    KoinComponent {

    private val notificationSchedulingService: NotificationSchedulingService by inject()
    private val analyticsService: AnalyticsService by inject()
    private val notificationService: AndroidNotificationService by inject()

    private val waitForLoadedContextModel: WaitForLoadedContextModel by inject()

    private val root = LifecycleGraph.Root(this)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep the splash screen visible until we're ready to display UI
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }

        // Do the minimal setup needed for launching the app
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set up the UI immediately
        setContent {
            MainView(waitForLoadedContextModel = waitForLoadedContextModel)

            var showSplashScreen by remember { mutableStateOf(true) }

            if (!showSplashScreen) {
                LaunchedEffect(Unit) {
                    askNotificationPermission()
                }
            }
            Crossfade(targetState = showSplashScreen) { shouldShowSplashScreen ->
                if (shouldShowSplashScreen) {
                    LaunchedEffect(Unit) {
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

        // Initialize the app after the UI is shown
        lifecycleScope.launch {
            // First tell the splash screen it can go away
            splashScreen.setKeepOnScreenCondition { false }

            // Log analytics after UI is displayed
            analyticsService.logEvent(AnalyticsService.EVENT_STARTED)

            // Now set up view model lifecycle
            waitForLoadedContextModel.lifecycle.removeFromParent()
            root.addChild(waitForLoadedContextModel.lifecycle)

            // Process any notification deeplinks
            notificationService.handleNotificationDeeplink(intent)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        lifecycleScope.launch {
            notificationService.handleNotificationDeeplink(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Workaround for a crash we could not reproduce: https://console.firebase.google.com/project/droidcon-148cc/crashlytics/app/android:co.touchlab.droidcon.london/issues/8c559569e69164d7109bd6b1be99ade5
        if (root.hasChild(waitForLoadedContextModel.lifecycle)) {
            root.removeChild(waitForLoadedContextModel.lifecycle)
        }
    }

    override fun onBackPressed() {
        if (!NavigationController.root.handleBackPress()) {
            super.onBackPressed()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // Permissions already granted, nothing to do
            } else if (/* DISABLED: */ false && shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: Not implemented yet: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
