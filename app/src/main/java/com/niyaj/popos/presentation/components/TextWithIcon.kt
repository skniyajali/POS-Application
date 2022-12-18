package com.niyaj.popos.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.niyaj.popos.presentation.ui.theme.SpaceMini

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    text: String = "",
    icon: ImageVector? = null,
    isTitle: Boolean = false,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if(text.isNotEmpty()) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = MaterialTheme.colors.primary,
                    modifier = iconModifier,
                )
                Spacer(modifier = Modifier.width(SpaceMini))
            }
            Text(
                text = text,
                fontFamily = if(text.startsWith("Email") || text.startsWith("Password")) FontFamily.Monospace else null,
                style = MaterialTheme.typography.body1,
                fontWeight = if(isTitle) FontWeight.SemiBold else fontWeight,
            )
        }
    }
}