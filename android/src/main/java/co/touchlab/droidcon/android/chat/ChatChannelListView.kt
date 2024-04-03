package co.touchlab.droidcon.android.chat

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import co.touchlab.droidcon.R
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.models.InitializationState

@Composable
fun ChatChannelListView(onChannelSelect: (String) -> Unit) {
    val client = ChatClient.instance()
    val clientInitialisationState by client.clientState.initializationState.collectAsState()

    when (clientInitialisationState) {
        InitializationState.COMPLETE -> {
            ChannelsScreen(
                title = stringResource(id = R.string.app_name),
                isShowingHeader = false,
                isShowingSearch = true,
                onItemClick = { onChannelSelect(it.cid) },
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