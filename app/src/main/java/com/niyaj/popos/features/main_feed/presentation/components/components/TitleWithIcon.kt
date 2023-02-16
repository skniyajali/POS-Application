package com.niyaj.popos.features.main_feed.presentation.components.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.niyaj.popos.features.components.TextWithIcon

@Composable
fun TitleWithIcon(
    modifier : Modifier = Modifier,
    textModifier : Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    showScrollToTop: Boolean = false,
    onClick: () -> Unit = {},
    onClickScrollToTop: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextWithIcon(
            modifier = textModifier,
            text = text,
            icon = icon,
            isTitle = true,
        )

        Row {
            AnimatedVisibility(
                visible = showScrollToTop,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                IconButton(
                    onClick = onClickScrollToTop,
                    modifier = iconModifier
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowCircleUp,
                        contentDescription = text,
                        tint = MaterialTheme.colors.primary,
                    )
                }
            }

            IconButton(
                onClick = onClick,
                modifier = iconModifier
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = text,
                    tint = MaterialTheme.colors.primary,
                )
            }
        }
    }
}