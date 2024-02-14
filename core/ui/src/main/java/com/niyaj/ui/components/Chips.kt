package com.niyaj.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.IconSizeMini
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMini

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StandardRoundedFilterChip(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    selected: Boolean = false,
    backgroundColor: Color = LightColor6,
    selectedColor: Color = MaterialTheme.colors.secondary,
    onClick: () -> Unit = {},
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = true,
        shape = RoundedCornerShape(SpaceMini),
        border = null,
        colors = ChipDefaults.filterChipColors(
            backgroundColor = backgroundColor,
            selectedContentColor = selectedColor,
        ),
        leadingIcon = {
            icon?.let {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colors.background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text.plus("icon"),
                        modifier = Modifier
                            .size(IconSizeMini)
                            .align(Alignment.Center)
                    )
                }
            }
        },
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
            )
        }
    )
}
