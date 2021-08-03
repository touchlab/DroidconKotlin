package co.touchlab.droidcon.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.android.ui.main.Main
import co.touchlab.droidcon.android.ui.theme.DroidconTheme
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.kermit.Kermit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class MainActivity: ComponentActivity(), KoinComponent {
    private val notificationSchedulingService: NotificationSchedulingService by inject()
    private val syncService: SyncService by inject()
    private val log: Kermit by inject { parametersOf("MainActivity") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenCreated {
            notificationSchedulingService.runScheduling()
        }

        lifecycleScope.launchWhenCreated {
            syncService.runSynchronization()
        }

        setContent {
            DroidconTheme {
                Main()
            }
        }
    }
}
