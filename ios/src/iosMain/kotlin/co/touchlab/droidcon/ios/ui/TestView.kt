package co.touchlab.droidcon.ios.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Application
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun TestView() {
    val (int, setInt) = remember { mutableStateOf(1) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Hello from Compose #$int!")
        Spacer(Modifier.weight(1f))
        Button(onClick = { setInt(int + 1) }) {
            Text("Increment")
        }
    }
}

fun getRootController() = Application("TestView") {
    TestView()
}