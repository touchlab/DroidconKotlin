package co.touchlab.droidcon.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import co.touchlab.droidcon.android.ui.theme.DroidconTheme
import co.touchlab.kermit.Kermit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity(), KoinComponent {
    private val log: Kermit by inject { parametersOf("MainActivity") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DroidconTheme {
                Text(text = "Hello World")
            }
        }
    }
}
