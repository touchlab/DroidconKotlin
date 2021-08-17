package co.touchlab.droidcon.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.android.ui.main.Main
import co.touchlab.droidcon.android.ui.theme.DroidconTheme
import co.touchlab.droidcon.android.viewModel.MainViewModel
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.kermit.Kermit
import com.google.accompanist.insets.ProvideWindowInsets
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class MainActivity: ComponentActivity(), KoinComponent {
    private val notificationSchedulingService: NotificationSchedulingService by inject()
    private val syncService: SyncService by inject()
    private val log: Kermit by inject { parametersOf("MainActivity") }
    private val analyticsService: AnalyticsService by inject()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        analyticsService.logEvent(AnalyticsService.EVENT_STARTED)

        lifecycleScope.launchWhenCreated {
            notificationSchedulingService.runScheduling()
        }

        lifecycleScope.launchWhenCreated {
            syncService.runSynchronization()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            DroidconTheme {
                ProvideWindowInsets {
                    Surface(color = MaterialTheme.colors.primary, modifier = Modifier.fillMaxSize()) {
                        Main(main = mainViewModel)
                    }
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mainViewModel.initializeFeedbackObserving()
    }
}
