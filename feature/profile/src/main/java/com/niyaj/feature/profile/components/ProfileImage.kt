package com.niyaj.feature.profile.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.ProfilePictureSizeLarge
import com.niyaj.model.RESTAURANT_LOGO

@Composable
fun ProfileImage(
    modifier : Modifier = Modifier,
    defaultLogo : Int = RESTAURANT_LOGO.toInt(),
    resLogo : Bitmap? = null,
) {
    val rainbowColorsBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF9575CD),
                Color(0xFFBA68C8),
                Color(0xFFE57373),
                Color(0xFFFFB74D),
                Color(0xFFFFF176),
                Color(0xFFAED581),
                Color(0xFF4DD0E1),
                Color(0xFF9575CD)
            )
        )
    }

    val borderWidth = 4.dp

    if (resLogo == null) {
        Image(
            painter = painterResource(id = defaultLogo),
            contentDescription = "logo",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(ProfilePictureSizeLarge)
                .clip(CircleShape)
                .border(BorderStroke(borderWidth, rainbowColorsBrush), CircleShape)
        )
    } else {
        Image(
            bitmap = resLogo.asImageBitmap(),
            contentDescription = "logo",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(ProfilePictureSizeLarge)
                .clip(CircleShape)
                .border(BorderStroke(borderWidth, rainbowColorsBrush), CircleShape)
        )
    }
}