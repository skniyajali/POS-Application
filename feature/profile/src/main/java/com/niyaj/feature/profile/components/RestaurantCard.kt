package com.niyaj.feature.profile.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.ProfilePictureSizeLarge
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.RestaurantInfo

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun RestaurantCard(
    modifier: Modifier = Modifier,
    info: RestaurantInfo,
    showPrintLogo: Boolean = false,
    bannerRes: ImageVector = ImageVector.vectorResource(id = R.drawable.banner),
    printLogo: Bitmap? = null,
    resLogo: Bitmap? = null,
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
        shape = RoundedCornerShape(SpaceSmall),
        backgroundColor = MaterialTheme.colors.onPrimary,
        elevation = 1.dp,
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
                Image(
                    imageVector = bannerRes,
                    contentDescription = "Banner Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(
                            y = ProfilePictureSizeLarge / 2
                        )
                ) {
                    ProfileImage(
                        modifier = Modifier,
                        resLogo = resLogo
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
                modifier = Modifier.padding(top = (ProfilePictureSizeLarge / 2)),
                info = info,
                printLogo = printLogo,
                showPrintLogo = showPrintLogo,
                onClickChangePrintLogo = onClickChangePrintLogo,
                onClickViewPrintLogo = onClickViewPrintLogo,
            )
        }
    }
}