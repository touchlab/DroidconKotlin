package co.touchlab.droidcon.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ui.icons.DateRange
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.viewmodel.session.BaseSessionListComponent
import co.touchlab.droidcon.viewmodel.session.BaseSessionListComponent.Child
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children

@Composable
internal fun SessionListView(component: BaseSessionListComponent) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Droidcon Berlin 2022") },
                elevation = 0.dp,
                modifier = Modifier.shadow(AppBarDefaults.TopAppBarElevation),
            )
        },
    ) { paddingValues ->
        Children(
            stack = component.stack,
            modifier = Modifier.padding(paddingValues),
        ) {
            when (val child = it.instance) {
                is Child.Loading -> EmptyView(text = "Loading...")
                is Child.Days -> SessionDaysView(days = child.component, modifier = Modifier.fillMaxSize())
                is Child.Empty -> EmptyView(text = "Sessions could not be loaded.")
            }
        }
    }
}

@Composable
private fun EmptyView(text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(Dimensions.Padding.default),
            tint = Color.Yellow,
        )

        Text(
            text = text,
            modifier = Modifier.padding(Dimensions.Padding.default),
            textAlign = TextAlign.Center,
        )
    }
}
