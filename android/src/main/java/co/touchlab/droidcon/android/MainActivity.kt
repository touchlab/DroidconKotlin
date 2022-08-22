package co.touchlab.droidcon.android

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
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.util.DefaultUrlHandler
import co.touchlab.droidcon.android.viewModel.MainViewModel
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.ui.uiModule
import co.touchlab.droidcon.ui.util.MainView
import co.touchlab.droidcon.util.UrlHandler
import co.touchlab.droidcon.viewmodel.ApplicationComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.google.accompanist.insets.ProvideWindowInsets
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module

class MainActivity: ComponentActivity(), KoinComponent {

    private val modules =
        module {
            single<ComponentContext> { defaultComponentContext() }
            single<UrlHandler> { DefaultUrlHandler(this@MainActivity) }
        } + uiModule

    init {
        loadKoinModules(modules)
    }

    private val notificationSchedulingService: NotificationSchedulingService by inject()
    private val syncService: SyncService by inject()
    private val analyticsService: AnalyticsService by inject()
    private val mainViewModel: MainViewModel by viewModels()

    private val applicationComponent: ApplicationComponent by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        analyticsService.logEvent(AnalyticsService.EVENT_STARTED)

        lifecycleScope.launchWhenCreated {
            notificationSchedulingService.runScheduling()
        }

        lifecycleScope.launchWhenCreated {
            syncService.runSynchronization()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Box(modifier = Modifier.background(Colors.primary)) {
                ProvideWindowInsets {
                    MainView(component = applicationComponent)
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
    }

    override fun onDestroy() {
        unloadKoinModules(modules)

        super.onDestroy()
    }
}
