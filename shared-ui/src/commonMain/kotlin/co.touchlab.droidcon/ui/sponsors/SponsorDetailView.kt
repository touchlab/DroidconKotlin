package co.touchlab.droidcon.ui.sponsors

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
import androidx.compose.material.AppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.ui.icons.ArrowBack
import co.touchlab.droidcon.ui.icons.Description
import co.touchlab.droidcon.ui.icons.Person
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.RemoteImage
import co.touchlab.droidcon.viewmodel.sponsor.SponsorDetailComponent
import co.touchlab.droidcon.viewmodel.sponsor.SponsorDetailComponent.Model
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@Composable
internal fun SponsorDetailView(component: SponsorDetailComponent) {
    val model by component.model.subscribeAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Sponsor") },
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
    }) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState),
        ) {
            HeaderView(name = model.name, groupTitle = model.groupName, imageUrl = model.imageUrl)

            model.abstract?.let {
                DescriptionView(description = it)
            }

            model.representatives.forEach { profile ->
                RepresentativeInfoView(profile = profile, onClick = { component.representativeTapped(profile) })
            }
        }
    }
}

@Composable
private fun HeaderView(name: String, groupTitle: String, imageUrl: Url?) {
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

        if (imageUrl != null) {
            RemoteImage(
                imageUrl = imageUrl.string,
                modifier = Modifier
                    .width(120.dp)
                    .padding(horizontal = Dimensions.Padding.default)
                    .clip(CircleShape)
                    .aspectRatio(1f),
                contentDescription = name,
            )
        } else {
            Image(
                imageVector = Icons.Default.Person,
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
}

@Composable
private fun DescriptionView(description: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
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
private fun RepresentativeInfoView(profile: Model.Representative, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val imageUrl = profile.avatarUrl
            if (imageUrl == null) {
                Image(
                    imageVector = Icons.Default.Person,
                    contentDescription = profile.info,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(80.dp)
                        .padding(start = Dimensions.Padding.default, end = Dimensions.Padding.default, top = Dimensions.Padding.half)
                        .clip(CircleShape)
                        .aspectRatio(1f)
                        .background(MaterialTheme.colors.primary),
                )
            } else {
                RemoteImage(
                    imageUrl = imageUrl.string,
                    contentDescription = profile.info,
                    modifier = Modifier
                        .width(80.dp)
                        .padding(start = Dimensions.Padding.default, end = Dimensions.Padding.default, top = Dimensions.Padding.half)
                        .clip(CircleShape)
                        .aspectRatio(1f)
                        .background(MaterialTheme.colors.primary),
                )
            }

            Text(
                text = profile.info,
                color = Color.Gray,
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
