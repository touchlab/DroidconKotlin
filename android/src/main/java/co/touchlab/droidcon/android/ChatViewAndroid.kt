package co.touchlab.droidcon.android

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.startActivity
import co.touchlab.droidcon.R
import co.touchlab.kermit.Logger
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.extensions.internal.addMember
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.ContainsFilterObject
import io.getstream.chat.android.models.FilterObject
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.models.Message
import io.getstream.result.call.enqueue

@Composable
internal fun ChatView() {

    val client = ChatClient.instance()

    val context: Context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Observe the client connection state
        val clientInitialisationState by client.clientState.initializationState.collectAsState()

        ChatTheme {
            when (clientInitialisationState) {
                InitializationState.COMPLETE -> {
                    ChannelsScreen(
                        title = stringResource(id = R.string.app_name),
                        isShowingHeader = false,
                        isShowingSearch = true,
                        onItemClick = { channel ->
                            startActivity(
                                context,
                                ChannelActivity.getIntent(
                                    context,
                                    channel.cid
                                ),
                                Bundle(),
                            )

                        },
                        onBackPressed = { /*finish()*/ }
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
    }
}