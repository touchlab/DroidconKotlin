package co.touchlab.droidcon.android.ui.schedule

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
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.main.ScheduleScreen
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar

@Composable
fun EventDetail(navController: NavHostController, eventId: Long) {
    Scaffold(
        topBar = { Toolbar(titleRes = R.string.schedule_event_detail_title, navController = navController, showBackButton = true) },
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState),
        ) {
            Box {
                Column {
                    Header()
                    Title()
                }
                var isEventAdded by remember { mutableStateOf(false) }
                FloatingActionButton(
                    onClick = { isEventAdded = !isEventAdded },
                    modifier = Modifier
                        .padding(top = 136.dp, start = Dimensions.Padding.default)
                        .size(44.dp),
                ) {
                    val iconRes = if (isEventAdded) R.drawable.ic_baseline_check_24 else R.drawable.ic_baseline_add_24
                    Icon(painter = painterResource(id = iconRes), contentDescription = "Add")
                }
            }
            Info()
            Description()
            listOf(
                Triple(
                    ImageBitmap.imageResource(id = R.drawable.about_kotlin),
                    "John Doe",
                    "Fusce interdum ultrices vulputate. Vestibulum sagittis dui vel hendrerit ornare. Duis et consectetur nunc. Curabitur pharetra blandit est, quis aliquam ligula convallis et. Nullam a vulputate leo, vitae facilisis nibh. Mauris leo nunc, maximus vel vestibulum ut, suscipit vitae nisi. Nulla facilisi.",
                ),
                Triple(
                    ImageBitmap.imageResource(id = R.drawable.about_kotlin),
                    "Janette Doe",
                    "Fusce interdum ultrices vulputate. Vestibulum sagittis dui vel hendrerit ornare. Duis et consectetur nunc. Curabitur pharetra blandit est, quis aliquam ligula convallis et. Nullam a vulputate leo, vitae facilisis nibh. Mauris leo nunc, maximus vel vestibulum ut, suscipit vitae nisi. Nulla facilisi.",
                ),
            ).forEach { speaker ->
                Speaker(image = speaker.first, name = speaker.second, description = speaker.third) {
                    navController.navigate(ScheduleScreen.SpeakerDetail.createRoute(25L))
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(color = Colors.teal),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Event cool title that can be quite long",
            style = MaterialTheme.typography.h4,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            modifier = Modifier.padding(
                start = Dimensions.Padding.double,
                end = Dimensions.Padding.double,
                top = Dimensions.Padding.default,
            ),
        )
        Text(
            text = "Robertson 1, 9:20 - 11:00 AM",
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
private fun Title() {
    Text(
        text = "Event cool title that can be quite long, not gonna lie",
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
private fun Info() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.menu_info),
            contentDescription = "Info",
            modifier = Modifier
                .padding(Dimensions.Padding.half)
                .width(64.dp),
            tint = Color.Black,
        )
        Text(
            text = "This session has already ended",
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(
                end = Dimensions.Padding.default,
                top = Dimensions.Padding.half,
                bottom = Dimensions.Padding.half,
            ),
            color = Color.Black,
        )
    }
}

@Composable
private fun Description() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            painter = painterResource(id = R.drawable.ic_description_black_24dp),
            contentDescription = "Info",
            modifier = Modifier
                .padding(Dimensions.Padding.half)
                .width(64.dp),
        )
        Text(
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam malesuada, nibh vel molestie fringilla, sapien neque " +
                "iaculis dolor, in congue nisl neque at dolor. Maecenas tincidunt, odio tristique viverra auctor, magna risus posuere justo, " +
                "ac aliquam neque augue non elit. In ut arcu et risus ullamcorper eleifend sed ac risus. Sed sit amet iaculis purus. " +
                "Proin convallis congue viverra. Ut eu nulla eget lectus sagittis pulvinar a sed tortor. Maecenas sodales ex ut ornare tempor.",
            modifier = Modifier.padding(
                end = Dimensions.Padding.default,
                top = Dimensions.Padding.half,
                bottom = Dimensions.Padding.half,
            ),
        )
    }
}

@Composable
private fun Speaker(image: ImageBitmap, name: String, description: String, speakerTapped: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { speakerTapped() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_background),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .padding(start = Dimensions.Padding.default, end = Dimensions.Padding.default, top = Dimensions.Padding.half)
                    .clip(CircleShape)
                    .aspectRatio(1f),
            )
            Text(
                text = name,
                color = Colors.grey,
                modifier = Modifier.padding(
                    end = Dimensions.Padding.default,
                    top = Dimensions.Padding.half,
                    bottom = Dimensions.Padding.half,
                ),
            )
        }
        Text(
            text = description,
            modifier = Modifier.padding(
                start = 80.dp,
                end = Dimensions.Padding.default,
                bottom = Dimensions.Padding.half,
            ),
        )
    }
}