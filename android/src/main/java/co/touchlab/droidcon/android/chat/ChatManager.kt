package co.touchlab.droidcon.android.chat

import android.content.Context
import co.touchlab.droidcon.UserData
import co.touchlab.kermit.Logger
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.extensions.internal.addMember
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.getstream.result.Error
import io.getstream.result.call.Call
import io.getstream.result.call.enqueue
import io.getstream.result.extractCause
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

object ChatManager {
    private val chatLogger: Logger = Logger.withTag("ChatManager")

    var isConnected: Boolean = false
        private set

    private val scope = CoroutineScope(Dispatchers.Default)

    fun initChat(
        applicationContext: Context,
        userData: UserData,
    ) {
        if (!isConnected) {
            chatLogger.i { "Initializing Chat" }
            // 1 - Set up the OfflinePlugin for offline storage
            val offlinePluginFactory = StreamOfflinePluginFactory(appContext = applicationContext)
            val statePluginFactory =
                StreamStatePluginFactory(
                    config = StatePluginConfig(),
                    appContext = applicationContext
                )

            // 2 - Set up the client for API calls and with the plugin for offline storage
            val client = ChatClient.Builder("3rbey5kf2r9z", applicationContext)
                .withPlugins(offlinePluginFactory, statePluginFactory)
                .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
                .build()

            chatLogger.i { "Built the client, adding user" }
            // 3 - Authenticate and connect the user
            val user = User(
                id = userData.id,
                name = userData.name ?: "Unknown Name",
                image = userData.pictureUrl ?: "",
                role = "ADMIN",
            )
            chatLogger.v { "Creating Dev Token" }
            val token = client.devToken(user.id)

            chatLogger.i { "CONNECTING USER!" }
            client.connectUser(user = user, token = token).enqueue(
                onSuccess = {
                    chatLogger.i { "SUCCESS! $it" }
                    isConnected = true
                }, onError = {
                    chatLogger.i { "ERROR! $it" }
                    isConnected = false
                })
        }
    }
}

suspend fun <T : Any> Call<T>.enqueueAsync(): T {
    return suspendCoroutine { continuation ->
        this.enqueue(onSuccess = { result: T ->
            continuation.resumeWith(Result.success(result))
        }, onError = { error: Error ->
            continuation.resumeWith(
                Result.failure(error.extractCause() ?: Throwable(message = error.message))
            )
        })
    }
}