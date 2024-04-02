package co.touchlab.droidcon.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChatView() {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {

        }){
            Text("Create Channel")
        }
    }
}