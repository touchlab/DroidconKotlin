package co.touchlab.droidcon.android

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import co.touchlab.droidcon.BuildConfig
import co.touchlab.droidcon.UserContext
import co.touchlab.droidcon.UserData
import co.touchlab.droidcon.domain.service.UserIdProvider
import co.touchlab.kermit.Logger
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FirebaseService : KoinComponent {

    private val auth: FirebaseAuth = Firebase.auth
    private val logger = Logger.withTag("Authentication")
    private val userIdProvider: UserIdProvider by inject()
    private val clientId = BuildConfig.CLIENT_ID
    
    fun performGoogleLogin(
        activity: Activity,
        resultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
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
                    resultLauncher.launch(request)
                } catch (e: IntentSender.SendIntentException) {
                    logger.e(e) { "Couldn't Start Intent" }
                }
            }
            .addOnFailureListener(activity) { e ->
                logger.e(e) { "Failed to Sign in" }
            }
    }

    fun handleResultTask(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            logger.d { "signInWithCredential:success" }
            auth.currentUser?.let { user ->
                saveCredentials(user)
            }
        } else {
            logger.e(task.exception) { "signInWithCredential:failure" }
        }
    }

    fun performLogout() {
        Firebase.auth.signOut()
    }

    fun saveCredentials(firebaseUser: FirebaseUser?) {
        userIdProvider.saveUserContext(
            UserContext(
                isAuthenticated = firebaseUser != null,
                userData = firebaseUser?.let {
                    UserData(
                        id = it.uid,
                        name = it.displayName,
                        email = it.email,
                        pictureUrl = it.photoUrl?.toString()
                    )
                }
            )
        )
    }
}