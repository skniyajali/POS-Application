package com.niyaj.feature.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.niyaj.designsystem.theme.ProfilePictureSizeLarge
import com.niyaj.model.RESTAURANT_LOGO
import java.io.File

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    logo: String,
) {
    val context = LocalContext.current
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

    val data = if (logo.isEmpty()) {
        RESTAURANT_LOGO.toInt()
    } else {
        File(context.filesDir, logo)
    }

    val logoRequest = ImageRequest
        .Builder(context)
        .data(data)
        .crossfade(enable = true)
        .placeholder(RESTAURANT_LOGO.toInt())
        .error(RESTAURANT_LOGO.toInt())
        .build()

    SubcomposeAsyncImage(
        model = logoRequest,
        contentDescription = "logo",
        contentScale = ContentScale.Crop,
        loading = { CircularProgressIndicator() },
        modifier = modifier
            .size(ProfilePictureSizeLarge)
            .clip(CircleShape)
            .border(BorderStroke(borderWidth, rainbowColorsBrush), CircleShape)
    )
}