package co.touchlab.droidcon.android.ui.sponsors

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.feedback.Feedback
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar

@Composable
fun Sponsors(navController: NavHostController) {
    Feedback()

    Scaffold(topBar = {
        Toolbar(titleRes = R.string.sponsors_title, navController = navController)
    }) {
        Text(
            text = "Coming soon!",
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.Padding.default),
            textAlign = TextAlign.Center,
        )
    }
}