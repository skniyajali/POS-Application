package com.niyaj.popos.features.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.SpaceMini

@Composable
fun StandardButton(
    modifier: Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(SpaceMini),
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(ButtonSize),
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = iconModifier
            )
            Spacer(modifier = Modifier.width(SpaceMini))
        }
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.button,
        )
    }
}


@Composable
fun StandardOutlinedButton(
    modifier: Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(SpaceMini),
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.error),
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = colors,
        border = border,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(ButtonSize),
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = iconModifier
            )
            Spacer(modifier = Modifier.width(SpaceMini))
        }
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.button,
        )
    }
}