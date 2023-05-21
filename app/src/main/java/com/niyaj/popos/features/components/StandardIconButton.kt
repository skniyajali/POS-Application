package com.niyaj.popos.features.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun StandardIconButton(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    imageVector: ImageVector,
    tint: Color = MaterialTheme.colors.onPrimary,
    enabled: Boolean = true,
    contentDescription: String? = null,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
    ){
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = iconModifier,
            tint = tint,
        )
    }
}


@Composable
fun StandardIconButtonPrimary(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    imageVector: ImageVector,
    tint : Color = MaterialTheme.colors.primary,
    enabled: Boolean = true,
    contentDescription: String? = null,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
    ){
        Icon(
            imageVector = imageVector,
            modifier = iconModifier,
            contentDescription = contentDescription ?: imageVector.name,
            tint = tint,
        )
    }
}