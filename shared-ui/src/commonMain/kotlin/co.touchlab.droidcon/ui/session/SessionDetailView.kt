package co.touchlab.droidcon.ui.session

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
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.dto.WebLink
import co.touchlab.droidcon.ui.icons.Add
import co.touchlab.droidcon.ui.icons.ArrowBack
import co.touchlab.droidcon.ui.icons.Check
import co.touchlab.droidcon.ui.icons.Description
import co.touchlab.droidcon.ui.icons.Info
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.RemoteImage
import co.touchlab.droidcon.ui.util.WebLinkText
import co.touchlab.droidcon.viewmodel.session.SessionDetailComponent
import co.touchlab.droidcon.viewmodel.session.SessionDetailComponent.Model
import co.touchlab.droidcon.viewmodel.session.SessionDetailComponent.SessionState
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@Composable
internal fun SessionDetailView(component: SessionDetailComponent) {
    val model by component.model.subscribeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Session") },
                elevation = 0.dp,
                modifier = Modifier.shadow(AppBarDefaults.TopAppBarElevation),
                navigationIcon = {
                    IconButton(onClick = component::backTapped) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },
    ) {
        val scrollState = rememberScrollState()
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            val state = model.state
            Box {
                Column {
                    HeaderView(model.title, model.info)
                }
                if (state != SessionState.Ended) {
                    val isAttending = model.isAttending
                    FloatingActionButton(
                        onClick = component::attendingTapped,
                        modifier = Modifier
                            .padding(top = 136.dp, start = Dimensions.Padding.default)
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
                SessionState.InConflict -> "This session is in conflict with another session in your schedule."
                SessionState.InProgress -> "This session is happening now."
                SessionState.Ended -> "This session has already ended."
                SessionState.None -> "This session hasn't started yet."
            }
            InfoView(status)

            if (model.showFeedbackOption) {
                Button(
                    onClick = component::writeFeedbackTapped,
                    modifier = Modifier
                        .padding(Dimensions.Padding.default)
                        .align(Alignment.CenterHorizontally),
                ) {
                    val text = if (model.feedbackAlreadyWritten) {
                        "Change your feedback"
                    } else {
                        "Add feedback"
                    }
                    Text(text = text)
                }
            }

            model.abstract?.let {
                DescriptionView(it, model.abstractLinks)
            }

            Text(
                text = "Speakers",
                modifier = Modifier.fillMaxWidth().padding(Dimensions.Padding.default),
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center,
            )

            Divider()

            model.speakers.forEach { speaker ->
                SpeakerView(speaker = speaker, onClick = { component.speakerTapped(speaker) })
            }
        }
    }
}

@Composable
private fun HeaderView(title: String, locationInfo: String) {
    Card(
        elevation = Dimensions.Padding.quarter,
        backgroundColor = MaterialTheme.colors.surface,
        shape = RectangleShape,
    ) {
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
                    start = Dimensions.Padding.double,
                    end = Dimensions.Padding.double,
                    top = Dimensions.Padding.default,
                ),
            )
            Text(
                text = locationInfo,
                modifier = Modifier.padding(
                    start = Dimensions.Padding.double,
                    end = Dimensions.Padding.double,
                    bottom = Dimensions.Padding.default,
                ),
            )
        }
    }
}

@Composable
private fun InfoView(status: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = Dimensions.Padding.default), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info",
            modifier = Modifier
                .padding(Dimensions.Padding.half)
                .width(64.dp),
            tint = MaterialTheme.colors.onSurface,
        )
        Text(
            text = status,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(
                end = Dimensions.Padding.default,
                top = Dimensions.Padding.half,
                bottom = Dimensions.Padding.half,
            ),
            color = MaterialTheme.colors.onSurface,
        )
    }
}

@Composable
private fun DescriptionView(description: String, links: List<WebLink>) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = "Description",
            modifier = Modifier
                .padding(Dimensions.Padding.half)
                .width(64.dp),
        )
        WebLinkText(
            text = description,
            links = links,
            modifier = Modifier.padding(
                end = Dimensions.Padding.default,
                top = Dimensions.Padding.half,
                bottom = Dimensions.Padding.half,
            ),
        )
    }
}

@Composable
private fun SpeakerView(speaker: Model.Speaker, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val imageUrl = speaker.avatarUrl?.string
            if (imageUrl != null) {
                RemoteImage(
                    imageUrl = imageUrl,
                    contentDescription = speaker.info,
                    modifier = Modifier.width(80.dp)
                        .padding(start = Dimensions.Padding.default, end = Dimensions.Padding.default, top = Dimensions.Padding.half)
                        .clip(CircleShape)
                        .aspectRatio(1f)
                        .background(MaterialTheme.colors.primary),
                )
            }

            Text(
                text = speaker.info,
                color = Color.Gray,
                modifier = Modifier.padding(
                    end = Dimensions.Padding.default,
                    top = Dimensions.Padding.half,
                    bottom = Dimensions.Padding.half,
                ),
            )
        }
        Text(
            text = speaker.bio ?: "",
            modifier = Modifier.padding(
                start = 80.dp,
                end = Dimensions.Padding.default,
                bottom = Dimensions.Padding.half,
            ),
        )
    }
}
