package co.touchlab.droidcon.android.ui.sponsors

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.main.SponsorsScreen
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar
import co.touchlab.droidcon.android.viewModel.sponsors.SponsorGroupViewModel
import co.touchlab.droidcon.android.viewModel.sponsors.SponsorListViewModel
import co.touchlab.droidcon.ui.theme.Colors
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import kotlin.math.min

@SuppressLint("DiscouragedApi")
@Composable
fun SponsorList(navController: NavHostController) {
    val sponsorList = viewModel<SponsorListViewModel>()

    val sponsorGroups by sponsorList.sponsorGroups.collectAsState(emptyList())
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(sponsorGroups) {
        selectedTabIndex = selectedTabIndex.coerceIn(sponsorGroups.indices.takeUnless { it.isEmpty() } ?: IntRange(0, 0))
    }

    Scaffold(
        topBar = {
            Toolbar(titleRes = R.string.sponsors_title, navController = navController)
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            if (sponsorGroups.isEmpty()) {
                Empty()
            } else {
                LazyColumn(contentPadding = PaddingValues(vertical = Dimensions.Padding.quarter)) {
                    items(sponsorGroups) { sponsorGroup ->
                        SponsorGroup(sponsorGroup, navController)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun SponsorGroup(sponsorGroup: SponsorGroupViewModel, navController: NavHostController) {
    val uriHandler = LocalUriHandler.current

    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(vertical = Dimensions.Padding.quarter, horizontal = Dimensions.Padding.half),
        color = MaterialTheme.colors.background,
        elevation = 2.dp,
        border = if (MaterialTheme.colors.isLight) null else BorderStroke(1.dp, MaterialTheme.colors.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = sponsorGroup.title,
                modifier = Modifier.padding(
                    start = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                    top = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.quarter
                ),
                style = MaterialTheme.typography.h4
            )
            val columnCount = if (sponsorGroup.isProminent) 3 else 4

            val sponsors by sponsorGroup.sponsors.collectAsState()

            repeat(sponsors.size / columnCount + if (sponsors.size % columnCount == 0) 0 else 1) { rowIndex ->
                Row(modifier = Modifier.padding(horizontal = Dimensions.Padding.half)) {
                    val startIndex = rowIndex * columnCount
                    val endIndex = min(startIndex + columnCount, sponsors.size)
                    sponsors.subList(startIndex, endIndex).forEach { sponsor ->

                        val resource = rememberImagePainter(
                            data = sponsor.imageUrl.string,
                            imageLoader = LocalImageLoader.current,
                            builder = {
                                placeholder(0)
                            }
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(Dimensions.Padding.quarter)
                                .shadow(2.dp, CircleShape)
                                .background(Color.White)
                                .clickable {
                                    if (sponsor.hasDetail) {
                                        navController.navigate(SponsorsScreen.Detail.createRoute(sponsor.id))
                                    } else if (sponsor.isUrlValid) {
                                        uriHandler.openUri(sponsor.url.string)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (resource.state !is ImagePainter.State.Success) {
                                Text(
                                    text = sponsor.name,
                                    modifier = Modifier.padding(Dimensions.Padding.half),
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 3
                                )
                            }
                            Image(
                                painter = resource,
                                contentDescription = sponsor.name
                            )
                        }
                    }
                    repeat(columnCount - endIndex + startIndex) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimensions.Padding.default))
        }
    }
}

@Composable
private fun Empty() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_calendar_today_24),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(Dimensions.Padding.default),
            tint = Colors.lightYellow
        )

        Text(
            text = stringResource(id = R.string.sponsors_empty),
            modifier = Modifier.padding(Dimensions.Padding.default),
            textAlign = TextAlign.Center
        )
    }
}
