package co.touchlab.droidcon.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import co.touchlab.droidcon.ui.icons.Info
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.LocalImage
import co.touchlab.droidcon.ui.util.WebLinkText
import co.touchlab.droidcon.viewmodel.settings.AboutComponent
import co.touchlab.droidcon.viewmodel.settings.AboutComponent.Model
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@Composable
internal fun AboutView(component: AboutComponent) {
    val model by component.model.subscribeAsState()
    model.items.forEach { aboutItem ->
        AboutItemView(aboutItem)
    }
}

@Composable
private fun AboutItemView(item: Model.Item) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            modifier = Modifier.padding(Dimensions.Padding.default),
            imageVector = Icons.Default.Info,
            contentDescription = item.title,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    top = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                ),
            )

            WebLinkText(
                text = item.detail,
                links = item.webLinks,
                modifier = Modifier.padding(end = Dimensions.Padding.default),
            )

            LocalImage(
                imageResourceName = item.icon,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        end = Dimensions.Padding.double,
                        top = Dimensions.Padding.default,
                        bottom = Dimensions.Padding.default,
                    ),
            )
        }
    }
}
