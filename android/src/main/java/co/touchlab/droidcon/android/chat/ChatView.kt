package co.touchlab.droidcon.android.chat

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.startActivity
import co.touchlab.droidcon.R
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.channel.state.ChannelState
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.models.InitializationState

@Composable
internal fun ChatView() {
    val context: Context = LocalContext.current
    var channelId: String? by remember {
        mutableStateOf(
            null
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ChatTheme {
            channelId?.let { cid ->
                MessagesScreen(
                    viewModelFactory = MessagesViewModelFactory(
                        context = context,
                        channelId = cid,
                        messageLimit = 30,
                    ),
                    onBackPressed = {
                        channelId = null
                    },
                )
            } ?: run {
                ChatChannelListView {
                    channelId = it
                }
            }
        }
    }
}