package co.touchlab.droidcon.ios.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import co.touchlab.droidcon.ios.viewmodel.session.SessionDetailViewModel
import co.touchlab.droidcon.ios.viewmodel.session.SpeakerListItemViewModel
import co.touchlab.droidcon.ios.viewmodel.settings.WebLink
import com.seiko.imageloader.ImageLoaderBuilder
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.rememberAsyncImagePainter

@Composable
internal fun SessionDetailTestView(viewModel: SessionDetailViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        val state by viewModel.observeState.observeAsState()
        Box {
            Column {
                val title by viewModel.observeTitle.observeAsState()
                val locationInfo by viewModel.observeInfo.observeAsState()
                HeaderView(title, locationInfo)
            }
            if (state != SessionDetailViewModel.SessionState.Ended) {
                val isAttending by viewModel.observeIsAttending.observeAsState()
                FloatingActionButton(
                    onClick = viewModel::attendingTapped,
                    modifier = Modifier
                        .padding(top = 136.dp, start = 16.dp)
                        .size(44.dp),
                ) {
                    val icon = if (isAttending) Icons.Default.Check else Icons.Default.Add
                    val description = if (isAttending) {
                        "Do not attend"
                    } else {
                        "Attend"
                    }
                    Icon(imageVector = icon, contentDescription = description)
                }
            }
        }

        val status = when (state) {
            SessionDetailViewModel.SessionState.InConflict -> "This session is in conflict with another session in your schedule."
            SessionDetailViewModel.SessionState.InProgress -> "This session is happening now."
            SessionDetailViewModel.SessionState.Ended -> "This session has already ended."
            null -> "This session hasn't started yet."
        }
        InfoView(status)

        val description by viewModel.observeAbstract.observeAsState()
        val descriptionLinks by viewModel.observeAbstractLinks.observeAsState()
        description?.let {
            DescriptionView(it, descriptionLinks)
        }

        Text(
            text = "Speakers",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
        )

        Divider()

        val speakers by viewModel.observeSpeakers.observeAsState()
        speakers.forEach { speaker ->
            SpeakerView(speaker)
        }
    }
}

@Composable
private fun HeaderView(title: String, locationInfo: String) {
    Card(elevation = 4.dp, backgroundColor = Color.hsl(hue = 0f, saturation = 0f, lightness = 0.96f), shape = RectangleShape) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h5,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(
                    start = 32.dp,
                    end = 32.dp,
                    top = 16.dp,
                ),
            )
            Text(
                text = locationInfo,
                modifier = Modifier.padding(
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 16.dp,
                ),
            )
        }
    }
}

@Composable
private fun InfoView(status: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info",
            modifier = Modifier
                .padding(8.dp)
                .width(64.dp),
            tint = MaterialTheme.colors.onSurface,
        )
        Text(
            text = status,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp,
            ),
            color = MaterialTheme.colors.onSurface,
        )
    }
}

@Composable
private fun DescriptionView(description: String, links: List<WebLink>) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.List,
            contentDescription = "Description",
            modifier = Modifier
                .padding(8.dp)
                .width(64.dp),
        )
        WebLinkText(
            text = description,
            links = links,
            modifier = Modifier.padding(
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp,
            ),
        )
    }
}

@Composable
private fun SpeakerView(speaker: SpeakerListItemViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { speaker.selected() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val imageUrl = speaker.avatarUrl?.string
            if (imageUrl != null) {
                CompositionLocalProvider(
                    LocalImageLoader provides ImageLoaderBuilder().build(),
                ) {
                    val resource = rememberAsyncImagePainter(
                        url = imageUrl.toString(),
                        imageLoader = LocalImageLoader.current,
                    )

                    Image(
                        painter = resource,
                        contentDescription = speaker.info,
                        modifier = Modifier.width(80.dp)
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                            .clip(CircleShape)
                            .aspectRatio(1f)
                            .background(Color.Cyan),
                    )
                }
            }

            Text(
                text = speaker.info,
                color = Color.Gray,
                modifier = Modifier.padding(
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                ),
            )
        }
        Text(
            text = speaker.bio ?: "",
            modifier = Modifier.padding(
                start = 80.dp,
                end = 16.dp,
                bottom = 8.dp,
            ),
        )
    }
}

fun getRootController(viewModel: SessionDetailViewModel) =
    Application("SessionDetailTestView") {
        SessionDetailTestView(viewModel)
    }
