package co.touchlab.droidcon.android.ui.sponsors

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import co.touchlab.droidcon.android.ui.main.SponsorsScreen
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar
import co.touchlab.droidcon.android.viewModel.sessions.ProfileViewModel
import co.touchlab.droidcon.android.viewModel.sponsors.SponsorDetailViewModel
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Sponsor
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun SponsorDetail(navController: NavHostController, sponsorId: Sponsor.Id) {
    val sponsorDetail = viewModel<SponsorDetailViewModel>()
    LaunchedEffect(sponsorId) {
        sponsorDetail.id.value = sponsorId
    }

    Scaffold(topBar = {
        Toolbar(titleRes = R.string.sponsor_title, navController = navController, showBackButton = true)
    }) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState),
        ) {
            val name by sponsorDetail.name.collectAsState(initial = "")
            val groupTitle by sponsorDetail.groupTitle.collectAsState(initial = "")
            val imageUrl by sponsorDetail.imageUrl.collectAsState(initial = null)
            Header(name = name, groupTitle = groupTitle, imageUrl = imageUrl)

            val description by sponsorDetail.description.collectAsState(initial = null)
            description?.let {
                Description(description = it)
            }

            val representatives by sponsorDetail.representatives.collectAsState(initial = emptyList())
            representatives.forEach {
                RepresentativeInfo(profile = it) {
                    navController.navigate(SponsorsScreen.RepresentativeDetail.createRoute(it.id))
                }
            }
        }
    }
}

@Composable
private fun Header(name: String, groupTitle: String, imageUrl: Url?) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp)
            .background(color = MaterialTheme.colors.primary),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.h5,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                modifier = Modifier.padding(
                    start = Dimensions.Padding.double,
                    top = Dimensions.Padding.default,
                ),
            )
            Text(
                text = groupTitle,
                color = Color.White,
                modifier = Modifier.padding(
                    start = Dimensions.Padding.double,
                    bottom = Dimensions.Padding.default,
                ),
            )
        }
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
    }
}

@Composable
private fun Description(description: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            painter = painterResource(id = R.drawable.ic_description_black_24dp),
            contentDescription = stringResource(id = R.string.sponsor_detail_description_description),
            modifier = Modifier
                .padding(Dimensions.Padding.half)
                .width(64.dp),
        )
        Text(
            text = description,
            modifier = Modifier.padding(
                end = Dimensions.Padding.default,
                top = Dimensions.Padding.half,
                bottom = Dimensions.Padding.half,
            ),
        )
    }
}

@Composable
private fun RepresentativeInfo(profile: ProfileViewModel, representativeTapped: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { representativeTapped() },
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val painter = profile.imageUrl?.string?.let { rememberCoilPainter(request = it) }
            Image(
                painter = painter ?: painterResource(id = R.drawable.ic_baseline_person_24),
                contentDescription = profile.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .padding(start = Dimensions.Padding.default, end = Dimensions.Padding.default, top = Dimensions.Padding.half)
                    .clip(CircleShape)
                    .aspectRatio(1f)
                    .background(Colors.teal),
            )

            Text(
                text = profile.title,
                color = Colors.grey,
                modifier = Modifier.padding(
                    end = Dimensions.Padding.default,
                    top = Dimensions.Padding.half,
                    bottom = Dimensions.Padding.half,
                ),
            )
        }
        Text(
            text = profile.bio ?: "",
            modifier = Modifier.padding(
                start = 80.dp,
                end = Dimensions.Padding.default,
                bottom = Dimensions.Padding.half,
            ),
        )
    }
}