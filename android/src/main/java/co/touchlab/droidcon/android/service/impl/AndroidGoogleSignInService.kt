package co.touchlab.droidcon.android.service.impl

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import co.touchlab.droidcon.BuildConfig
import co.touchlab.droidcon.domain.service.GoogleSignInService
import co.touchlab.kermit.Logger
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.brightify.hyperdrive.utils.WeakReference
import org.koin.core.component.KoinComponent

class AndroidGoogleSignInService : GoogleSignInService, KoinComponent {

    private val logger = Logger.withTag("AuthenticationService")
    private val clientId = BuildConfig.CLIENT_ID

    private lateinit var weakActivity: WeakReference<Activity>
    private lateinit var weakLauncher: WeakReference<ActivityResultLauncher<IntentSenderRequest>>

    fun setActivity(
        activity: Activity,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
    ) {
        weakActivity = WeakReference(activity)
        weakLauncher = WeakReference(launcher)
    }

    override fun performGoogleLogin(): Boolean {
        weakActivity.get()?.let { activity ->
            logger.i { "Performing Google Login" }
            val oneTapClient = Identity.getSignInClient(activity)
            val signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(clientId)
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .build()
            logger.v { "Beginning Sign In" }
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(activity) { result ->
                    logger.v { "Success! Starting Intent Sender" }
                    try {
                        val request = IntentSenderRequest
                            .Builder(result.pendingIntent.intentSender)
                            .build()
                        weakLauncher.get()?.launch(request)
                    } catch (e: IntentSender.SendIntentException) {
                        logger.e(e) { "Couldn't Start Intent" }
                    }
                }
                .addOnFailureListener(activity) { e ->
                    logger.e(e) { "Failed to Sign in" }
                }
            return true
        }
        return false
    }

    override fun performGoogleLogout(): Boolean {
        Firebase.auth.signOut()
        return true
    }
}
