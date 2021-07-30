package co.touchlab.droidcon.android.ui.sessions

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.dto.WebLink
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar
import co.touchlab.droidcon.android.ui.theme.WebLinkText

@Composable
fun SpeakerDetail(navController: NavHostController, speakerId: Long) {
    Scaffold(
        topBar = { Toolbar(titleRes = R.string.schedule_speaker_detail_title, navController = navController, showBackButton = true) },
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState),
        ) {
            Header()
            // speakerInfoList.forEach { speakerInfo ->
            //     SpeakerInfo(iconRes = speakerInfo.first, text = speakerInfo.second, isLink = speakerInfo.third)
            // }
        }
    }
}

@Composable
private fun Header() {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp)
            .background(color = Colors.teal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Adam Hurwitz",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(120.dp)
                .padding(horizontal = Dimensions.Padding.default)
                .clip(CircleShape)
                .aspectRatio(1f),
        )
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = "Adam Hurwitz",
                style = MaterialTheme.typography.h4,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                modifier = Modifier.padding(
                    end = Dimensions.Padding.double,
                    top = Dimensions.Padding.default,
                ),
            )
            Text(
                text = "Coinverse, Creator",
                color = Color.White,
                modifier = Modifier.padding(
                    end = Dimensions.Padding.double,
                    bottom = Dimensions.Padding.default,
                ),
            )
        }
    }
}

@Composable
private fun SpeakerInfo(@DrawableRes iconRes: Int, text: String, isLink: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "Info",
            modifier = Modifier
                .padding(Dimensions.Padding.half)
                .width(64.dp),
        )
        val links = if (isLink) listOf(WebLink(IntRange(0, text.length - 1), text)) else emptyList()
        WebLinkText(
            text = text,
            links = links,
            modifier = Modifier.padding(
                end = Dimensions.Padding.default,
                top = Dimensions.Padding.half,
                bottom = Dimensions.Padding.half,
            ),
        )
    }
}
