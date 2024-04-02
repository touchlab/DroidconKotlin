package co.touchlab.droidcon.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.service.impl.AndroidGoogleSignInService
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.AuthenticationService
import co.touchlab.droidcon.domain.service.GoogleSignInService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.service.AndroidNotificationService
import co.touchlab.droidcon.ui.theme.Colors
import co.touchlab.droidcon.ui.util.MainView
import co.touchlab.droidcon.util.AppChecker
import co.touchlab.droidcon.util.NavigationController
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import co.touchlab.kermit.Logger
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.getstream.result.call.enqueue
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.brightify.hyperdrive.multiplatformx.LifecycleGraph
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {

    private val notificationSchedulingService: NotificationSchedulingService by inject()
    private val syncService: SyncService by inject()
    private val analyticsService: AnalyticsService by inject()

    private val applicationViewModel: ApplicationViewModel by inject()
    private val root = LifecycleGraph.Root(this)
    private val authenticationService: AuthenticationService by inject()
    private val googleSignInService: GoogleSignInService by inject()

    private lateinit var auth: FirebaseAuth
    private val chatLogger: Logger = Logger.withTag("KevinChat")

    private val firebaseAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        firebaseAuth.currentUser?.let { user ->
            authenticationService.setCredentials(
                id = user.uid,
                name = user.displayName,
                email = user.email,
                pictureUrl = user.photoUrl?.toString(),
            )
            initChat(
                id = user.uid,
                name = user.displayName,
                email = user.email,
                pictureUrl = user.photoUrl?.toString(),
            )
        } ?: run { authenticationService.clearCredentials() }
    }

    private val firebaseIntentResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            val logger = Logger.withTag("Authentication")
            try {
                val oneTapClient = Identity.getSignInClient(baseContext)
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val firebaseCredential =
                    GoogleAuthProvider.getCredential(credential.googleIdToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            logger.d { "signInWithCredential:success" }
                            auth.currentUser?.let { user ->
                                authenticationService.setCredentials(
                                    id = user.uid,
                                    name = user.displayName,
                                    email = user.email,
                                    pictureUrl = user.photoUrl?.toString(),
                                )
                            }
                        } else {
                            logger.e(task.exception) { "signInWithCredential:failure" }
                        }
                    }
            } catch (e: ApiException) {
                logger.e(e) { "NO ID Token" }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
                setContent {
                    // Observe the client connection state
                    val clientInitialisationState by client.clientState.initializationState.collectAsState()

                    ChatTheme {
                        when (clientInitialisationState) {
                            InitializationState.COMPLETE -> {
                                ChannelsScreen(
                                    title = stringResource(id = R.string.app_name),
                                    isShowingSearch = true,
                                    onItemClick = { channel ->
                                        startActivity(ChannelActivity.getIntent(this, channel.cid))
                                    },
                                    onBackPressed = { finish() }
                                )
                            }

                            InitializationState.INITIALIZING -> {
                                Text(text = "Initialising...")
                            }

                            InitializationState.NOT_INITIALIZED -> {
                                Text(text = "Not initialized...")
                            }
                        }
                    }
                }*/


        auth = Firebase.auth
        auth.addAuthStateListener(firebaseAuthListener)
        installSplashScreen()
        AppChecker.checkTimeZoneHash()

        (googleSignInService as AndroidGoogleSignInService).setActivity(
            this,
            firebaseIntentResultLauncher
        )
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

    private fun initChat(
        id: String,
        name: String?,
        email: String?,
        pictureUrl: String?,
    ) {
        chatLogger.i { "Initializing Chat" }
        // 1 - Set up the OfflinePlugin for offline storage
        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = applicationContext)
        val statePluginFactory =
            StreamStatePluginFactory(config = StatePluginConfig(), appContext = this)

        // 2 - Set up the client for API calls and with the plugin for offline storage
        val client = ChatClient.Builder("3rbey5kf2r9z", applicationContext)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()


        chatLogger.i { "Built the client, adding user" }
        // 3 - Authenticate and connect the user
        val user = User( // TODO: Two devices create a Channel
            id = id,
            name = name ?: "Unknown Name",
            image = pictureUrl ?: "",
        )
        chatLogger.v { "Creating Dev Token" }
        val token = client.devToken(user.id)

        lifecycleScope.launch {
            Logger.i(tag = "KEVINUSER", messageString = "CONNECTING USER!")
            client.connectUser(
                user = user,
                token = token
            ).enqueue(onSuccess = {
                Logger.i(tag = "KEVINUSER", messageString = "SUCCESS!")


                client.createChannel(
                    channelType = "messaging",
                    channelId = "",
                    memberIds = listOf(user.id, "test"),
                    extraData = emptyMap()
                ).enqueue { result ->
                    if (result.isSuccess) {
                        Logger.i(tag = "KEVIN", messageString = "SIGNED IN ")
                    } else {
                        Logger.i(
                            tag = "KEVIN",
                            messageString = result.errorOrNull()?.toString() ?: "ERROR"
                        )
                    }
                }
            }, onError = {
                Logger.i(tag = "KEVINUSER", messageString = "ERROR!")
            })
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.let(::handleNotificationDeeplink)
    }

    private fun handleNotificationDeeplink(intent: Intent) {
        val type =
            intent.getStringExtra(AndroidNotificationService.NOTIFICATION_TYPE_EXTRA_KEY)
                ?: return
        val sessionId =
            intent.getStringExtra(AndroidNotificationService.NOTIFICATION_SESSION_ID_EXTRA_KEY)
                ?: return
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
        auth.removeAuthStateListener(firebaseAuthListener)
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
