package com.niyaj.feature.profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.PRINT_LOGO
import com.niyaj.model.RestaurantInfo
import com.niyaj.ui.components.NoteText
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedButton
import java.io.File


@Composable
fun RestaurantDetails(
    modifier: Modifier = Modifier,
    info: RestaurantInfo,
    showPrintLogo: Boolean = false,
    onClickChangePrintLogo: () -> Unit,
    onClickViewPrintLogo: () -> Unit,
) {
    val context = LocalContext.current

    val printLogoRequest = ImageRequest
        .Builder(context)
        .data(File(context.filesDir, info.printLogo))
        .crossfade(enable = true)
        .placeholder(PRINT_LOGO.toInt())
        .error(PRINT_LOGO.toInt())
        .build()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = info.name,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(SpaceMini))

        Text(
            text = info.tagline,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        Text(
            text = info.description,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        Text(
            text = info.address,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = SpaceMini)
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = info.primaryPhone,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )

            if (info.secondaryPhone.isNotEmpty()) {
                Text(text = " / ")
                Text(
                    text = info.secondaryPhone,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(modifier = Modifier.height(SpaceSmall))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        if (info.printLogo.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                NoteText(
                    text = "You have not set your print logo, Click below to set.",
                    onClick = onClickChangePrintLogo
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardButton(
                    text = "Set Image",
                    icon = Icons.Default.AddAPhoto,
                    onClick = onClickChangePrintLogo,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondaryVariant
                    )
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                NoteText(
                    text = "Restaurant print logo has been set, Click below to change",
                    color = MaterialTheme.colors.primary,
                    onClick = onClickChangePrintLogo
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StandardOutlinedButton(
                        text = "Change",
                        icon = Icons.Default.AddToPhotos,
                        onClick = onClickChangePrintLogo,
                    )

                    Spacer(modifier = Modifier.width(SpaceSmall))

                    StandardButton(
                        text = if (!showPrintLogo) "View Image" else "Hide Image",
                        icon = Icons.Default.ImageSearch,
                        onClick = onClickViewPrintLogo
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showPrintLogo && info.printLogo.isNotEmpty(),
        ) {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(SpaceSmall),
                backgroundColor = LightColor6
            ) {
                SubcomposeAsyncImage(
                    model = printLogoRequest,
                    contentDescription = "Print Logo",
                    loading = { CircularProgressIndicator() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

