package com.niyaj.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.MediumGray
import com.niyaj.designsystem.theme.SpaceMini

@Composable
fun RowScope.StandardBottomNavItem(
    modifier: Modifier = Modifier,
    selectedIcon: ImageVector? = null,
    deselectedIcon: ImageVector? = null,
    contentDescription: String,
    selected: Boolean = false,
    selectedColor: Color = MaterialTheme.colors.primary,
    unselectedColor: Color = MediumGray,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val lineLength = animateFloatAsState(
        targetValue = if(selected) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300
        ), label = ""
    )

    BottomNavigationItem(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        selectedContentColor = selectedColor,
        unselectedContentColor = unselectedColor,
        icon = {
            if(selectedIcon != null && deselectedIcon != null) {
                Icon(
                    imageVector = if (selected) selectedIcon else deselectedIcon,
                    contentDescription = contentDescription,
                )
            }
        },
        label = {
            Text(
                text = contentDescription,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier
            )
        },
        modifier = modifier
            .padding(SpaceMini)
            .drawBehind {
                if (lineLength.value > 0f) {
                    drawLine(
                        color = if (selected) selectedColor else unselectedColor,
                        start = Offset(
                            x = size.width / 2f - lineLength.value * 15.dp.toPx(),
                            y = size.height
                        ),
                        end = Offset(
                            x = size.width / 2f + lineLength.value * 15.dp.toPx(),
                            y = size.height
                        ),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            },
    )
}