package com.niyaj.popos.features.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.SpaceMini

@Composable
fun StandardButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .pressClickEffect()
            .fillMaxWidth()
            .heightIn(ButtonSize),
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text,
            )
            Spacer(modifier = Modifier.width(SpaceMini))
        }
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.button,
        )
    }
}