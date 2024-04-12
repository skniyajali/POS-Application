package com.niyaj.feature.profile.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
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

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun RestaurantCard(
    modifier: Modifier = Modifier,
    info: RestaurantInfo,
    showPrintLogo: Boolean = false,
    onClickEdit: () -> Unit,
    onClickChangePrintLogo: () -> Unit,
    onClickViewPrintLogo: () -> Unit,
) {
    val iconSize = 24.dp
    val offsetInPx = LocalDensity.current.run { (iconSize / 2).roundToPx() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.onPrimary),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = SpaceSmall,
            bottomEnd = SpaceSmall
        ),
        backgroundColor = MaterialTheme.colors.onPrimary,
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(MaterialTheme.colors.primary)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(
                            y = 30.dp
                        )
                ) {
                    ProfileImage(
                        modifier = Modifier,
                        logo = info.logo
                    )

                    IconButton(
                        onClick = onClickEdit,
                        modifier = Modifier
                            .offset {
                                IntOffset(x = +offsetInPx, y = -offsetInPx)
                            }
                            .size(iconSize)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Change Image",
                            tint = MaterialTheme.colors.background
                        )
                    }
                }
            }

            RestaurantDetails(
                modifier = Modifier
                    .padding(top = 30.dp),
                info = info,
                showPrintLogo = showPrintLogo,
                onClickChangePrintLogo = onClickChangePrintLogo,
                onClickViewPrintLogo = onClickViewPrintLogo,
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun UpdatedRestaurantCard(
    modifier: Modifier = Modifier,
    info: RestaurantInfo,
    showPrintLogo: Boolean = false,
    onClickEdit: () -> Unit,
    onClickChangePrintLogo: () -> Unit,
    onClickViewPrintLogo: () -> Unit,
) {
    val context = LocalContext.current
    val iconSize = 24.dp
    val offsetInPx = LocalDensity.current.run { (iconSize / 2).roundToPx() }

    val printLogoRequest = ImageRequest
        .Builder(context)
        .data(File(context.filesDir, info.printLogo))
        .crossfade(enable = true)
        .placeholder(PRINT_LOGO.toInt())
        .error(PRINT_LOGO.toInt())
        .build()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.onPrimary),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = SpaceSmall,
            bottomEnd = SpaceSmall
        ),
        backgroundColor = MaterialTheme.colors.onPrimary,
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(MaterialTheme.colors.primary)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(
                            y = 30.dp
                        )
                ) {
                    ProfileImage(
                        modifier = Modifier,
                        logo = info.logo
                    )

                    IconButton(
                        onClick = onClickEdit,
                        modifier = Modifier
                            .offset {
                                IntOffset(x = +offsetInPx, y = -offsetInPx)
                            }
                            .size(iconSize)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Change Image",
                            tint = MaterialTheme.colors.background
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

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

            Crossfade(
                targetState = info.printLogo.isEmpty(),
                label = "isPrintLogoEmpty"
            ) {
                if (it) {
                    Divider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                    ) {
                        NoteText(
                            text = "You have not set your print logo, Click below to set.",
                            onClick = onClickChangePrintLogo
                        )

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
}