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

    fun initChat(
        applicationContext: Context,
        userData: UserData,
    ) {
        if (!isConnected) {
            chatLogger.i { "Initializing Chat" }
            val offlinePluginFactory = StreamOfflinePluginFactory(appContext = applicationContext)
            val statePluginFactory =
                StreamStatePluginFactory(
                    config = StatePluginConfig(),
                    appContext = applicationContext
                )

            val client = ChatClient.Builder("3rbey5kf2r9z", applicationContext)
                .withPlugins(offlinePluginFactory, statePluginFactory)
                .logLevel(ChatLogLevel.ALL) // TODO: Set to NOTHING in prod
                .build()

            chatLogger.v { "Built the client, adding user" }
            val user = User(
                id = userData.id,
                name = userData.name ?: "Unknown Name",
                image = userData.pictureUrl ?: "",
                role = "ADMIN",
            )
            val token = client.devToken(user.id) // TODO: Replace

            chatLogger.v { "Connecting User" }
            client.connectUser(user = user, token = token).enqueue(
                onSuccess = {
                    chatLogger.v { "Successfully Connected!" }
                    isConnected = true
                    joinDefaultChannels(user.id)
                }, onError = {
                    chatLogger.e { "Error Connecting! $it" }
                    isConnected = false
                })
        }
    }

    private fun joinDefaultChannels(id: String) {
        chatLogger.i { "Joining the General Channel" }
        val channelClient = ChatClient.instance().channel("messaging", "general")
        channelClient.addMembers(listOf(id)).enqueue(
            onSuccess = {
                chatLogger.v { "Successfully Joined The General channel" }
            },
            onError = {
                chatLogger.e { "Error Joining the General channel $it" }
            })
    }
}