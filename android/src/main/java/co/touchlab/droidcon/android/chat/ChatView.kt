package co.touchlab.droidcon.android.chat

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.startActivity
import co.touchlab.droidcon.R
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.InitializationState

@Composable
internal fun ChatView() {
    val context: Context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        ChatTheme {
            val client = ChatClient.instance()
            val clientInitialisationState by client.clientState.initializationState.collectAsState()

            when (clientInitialisationState) {
                InitializationState.COMPLETE -> {
                    ChannelsScreen(
                        title = stringResource(id = R.string.app_name),
                        isShowingHeader = false,
                        isShowingSearch = true,
                        onItemClick = {
                            startActivity(
                                context,
                                ChannelActivity.getIntent(context, it.cid),
                                Bundle(),
                            )
                        },
                        onBackPressed = { }
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
