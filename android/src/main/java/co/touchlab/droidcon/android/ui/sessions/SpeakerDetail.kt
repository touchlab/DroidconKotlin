package co.touchlab.droidcon.android.ui.sessions

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar
import co.touchlab.droidcon.android.ui.theme.WebLinkText
import co.touchlab.droidcon.android.viewModel.sessions.SpeakerDetailViewModel
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Profile
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun SpeakerDetail(navController: NavHostController, speakerId: Profile.Id) {
    val speakerDetail = viewModel<SpeakerDetailViewModel>()
    LaunchedEffect(speakerId) {
        speakerDetail.id.value = speakerId
    }

    Scaffold(
        topBar = { Toolbar(titleRes = R.string.schedule_speaker_detail_title, navController = navController, showBackButton = true) },
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState),
        ) {
            val name by speakerDetail.name.collectAsState("")
            val tagLine by speakerDetail.tagLine.collectAsState("")
            val imageUrl by speakerDetail.imageUrl.collectAsState(null)
            Header(name, tagLine, imageUrl)

            val infoList by speakerDetail.infoList.collectAsState(emptyList())
            infoList.forEach { speakerInfo ->
                SpeakerInfo(speakerInfo)
            }
        }
    }
}

@Composable
private fun Header(name: String, tagLine: String, imageUrl: Url?) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp)
            .background(color = Colors.teal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val painter = imageUrl?.string?.let { rememberCoilPainter(request = it) }
        Image(
            painter = painter ?: painterResource(id = R.drawable.ic_baseline_person_24),
            contentDescription = name,
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
                text = name,
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
                text = tagLine,
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
private fun SpeakerInfo(info: SpeakerDetailViewModel.SpeakerInfo) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            painter = painterResource(id = info.iconRes),
            contentDescription = stringResource(id = R.string.schedule_speaker_detail_info_description),
            modifier = Modifier
                .padding(Dimensions.Padding.half)
                .width(64.dp),
        )
        WebLinkText(
            text = info.text,
            links = info.links,
            modifier = Modifier.padding(
                end = Dimensions.Padding.default,
                top = Dimensions.Padding.half,
                bottom = Dimensions.Padding.half,
            ),
        )
    }
}
