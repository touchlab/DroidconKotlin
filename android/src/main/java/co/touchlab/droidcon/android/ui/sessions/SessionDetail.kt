package co.touchlab.droidcon.android.ui.sessions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.dto.WebLink
import co.touchlab.droidcon.android.ui.feedback.Feedback
import co.touchlab.droidcon.android.ui.main.ScheduleScreen
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar
import co.touchlab.droidcon.android.ui.theme.WebLinkText
import co.touchlab.droidcon.android.viewModel.sessions.ProfileViewModel
import co.touchlab.droidcon.android.viewModel.sessions.SessionDetailViewModel
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.ui.theme.Colors
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter

@Composable
fun SessionDetail(navController: NavHostController, sessionId: Session.Id) {
    val sessionDetail = viewModel<SessionDetailViewModel>()
    LaunchedEffect(sessionId) {
        sessionDetail.id.value = sessionId
    }
    val feedback by sessionDetail.showFeedback.collectAsState()
    feedback?.let {
        Feedback(it)
    }

    Scaffold(
        topBar = { Toolbar(titleRes = R.string.schedule_session_detail_title, navController = navController, showBackButton = true) },
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState),
        ) {
            Box {
                Column {
                    val title by sessionDetail.title.collectAsState("")
                    val locationInfo by sessionDetail.locationInfo.collectAsState("")
                    Header(title, locationInfo)
                    Title(title)
                }
                val hasEnded by sessionDetail.hasEnded.collectAsState(true)
                if (!hasEnded) {
                    val isAttending by sessionDetail.isAttending.collectAsState(false)
                    FloatingActionButton(
                        onClick = { sessionDetail.toggleIsAttending(!isAttending) },
                        modifier = Modifier
                            .padding(top = 136.dp, start = Dimensions.Padding.default)
                            .size(44.dp),
                    ) {
                        val iconRes = if (isAttending) R.drawable.ic_baseline_check_24 else R.drawable.ic_baseline_add_24
                        val descriptionRes = if (isAttending) {
                            R.string.schedule_session_detail_is_attending_action_description
                        } else {
                            R.string.schedule_session_detail_is_not_attending_action_description
                        }
                        Icon(painter = painterResource(id = iconRes), contentDescription = stringResource(id = descriptionRes))
                    }
                }
            }

            val statusRes by sessionDetail.statusRes.collectAsState(null)
            Info(statusRes?.let { stringResource(id = it) } ?: "")

            val showFeedbackOption by sessionDetail.showFeedbackOption.collectAsState()
            if (showFeedbackOption) {
                Button(
                    onClick = sessionDetail::writeFeedbackTapped,
                    modifier = Modifier
                        .padding(Dimensions.Padding.default)
                        .align(Alignment.CenterHorizontally),
                ) {
                    val showFeedbackTitleRes by sessionDetail.showFeedbackTitleRes.collectAsState()
                    Text(text = stringResource(id = showFeedbackTitleRes))
                }
            }

            val description by sessionDetail.description.collectAsState("" to emptyList())
            Description(description.first, description.second)

            val speakers by sessionDetail.speakers.collectAsState(emptyList())
            speakers.forEach { speaker ->
                Speaker(speaker) {
                    navController.navigate(ScheduleScreen.SpeakerDetail.createRoute(speaker.id))
                }
            }
        }
    }
}

@Composable
private fun Header(title: String, locationInfo: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(color = MaterialTheme.colors.primary),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h5,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            modifier = Modifier.padding(
                start = Dimensions.Padding.double,
                end = Dimensions.Padding.double,
                top = Dimensions.Padding.default,
            ),
        )
        Text(
            text = locationInfo,
            color = Color.White,
            modifier = Modifier.padding(
                start = Dimensions.Padding.double,
                end = Dimensions.Padding.double,
                bottom = Dimensions.Padding.default,
            ),
        )
    }
}

@Composable
private fun Title(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(
            start = 80.dp,
            end = Dimensions.Padding.default,
            top = Dimensions.Padding.default,
            bottom = Dimensions.Padding.default,
        ),
        style = MaterialTheme.typography.subtitle1,
        color = Colors.grey,
    )
}

@Composable
private fun Info(status: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.menu_info),
            contentDescription = stringResource(id = R.string.schedule_session_detail_info_description),
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
private fun Description(description: String, links: List<WebLink>) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            painter = painterResource(id = R.drawable.ic_description_black_24dp),
            contentDescription = stringResource(id = R.string.schedule_session_detail_description_description),
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

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun Speaker(speaker: ProfileViewModel, speakerTapped: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { speakerTapped() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            val painter = speaker.imageUrl?.string?.let {
                rememberImagePainter(
                    data = it,
                    imageLoader = LocalImageLoader.current,
                    builder = {
                        placeholder(0)
                    }
                )
            }

            Image(
                painter = painter ?: painterResource(id = R.drawable.ic_baseline_person_24),
                contentDescription = speaker.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .padding(start = Dimensions.Padding.default, end = Dimensions.Padding.default, top = Dimensions.Padding.half)
                    .clip(CircleShape)
                    .aspectRatio(1f)
                    .background(Colors.teal),
            )
            Text(
                text = speaker.title,
                color = Colors.grey,
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
