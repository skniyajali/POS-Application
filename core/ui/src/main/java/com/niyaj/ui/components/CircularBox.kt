package com.niyaj.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.getAllCapitalizedLetters
import com.niyaj.designsystem.theme.IconSizeMedium
import com.niyaj.designsystem.theme.IconSizeSmall

@Composable
fun CircularBox(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    doesSelected: Boolean,
    text: String? = null,
    showBorder: Boolean = false,
    size: Dp = 40.dp,
    selectedIcon: ImageVector = Icons.Default.Check,
    backgroundColor: Color = MaterialTheme.colors.background,
    selectedTint: Color = MaterialTheme.colors.secondaryVariant,
    unselectedTint: Color = MaterialTheme.colors.secondaryVariant,
    borderStroke: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.error),
) {
    val availBorder = if (showBorder) borderStroke  else null

    val textStyle =
        if (size < 40.dp) MaterialTheme.typography.body2 else MaterialTheme.typography.body1
    val iconSize = if (size < 40.dp) IconSizeSmall else IconSizeMedium

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(availBorder?.let {
                Modifier.border(it, CircleShape)
            } ?: Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (text.isNullOrEmpty()) {
            Icon(
                imageVector = if (doesSelected) selectedIcon else icon,
                contentDescription = "",
                tint = if (doesSelected) selectedTint else unselectedTint,
                modifier = Modifier.size(iconSize)
            )
        } else {
            if (doesSelected) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = "",
                    tint = selectedTint,
                    modifier = Modifier.size(iconSize)
                )
            } else {
                Text(
                    text = getAllCapitalizedLetters(text).take(2),
                    style = textStyle
                )
            }
        }
    }
}