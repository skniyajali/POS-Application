package com.niyaj.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.IconSizeSmall
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun IconBox(
    text: String,
    icon: ImageVector? = null,
    selected: Boolean = false,
    borderColor: Color = MaterialTheme.colors.primary,
    onClick: () -> Unit = {},
) {
    val borderStroke = if (selected) BorderStroke(1.dp, borderColor) else BorderStroke(0.dp, Color.Transparent)
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(2.dp),
        border = borderStroke,
        backgroundColor = LightColor8
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
           icon?.let {
               Icon(
                   imageVector = icon,
                   contentDescription = text,
                   tint = MaterialTheme.colors.primary,
                   modifier = Modifier.size(IconSizeSmall)
               )
               Spacer(modifier = Modifier.width(SpaceSmall))
           }

            Text(
                text = text,
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.primary
            )
        }
    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun IconBox(
    modifier: Modifier = Modifier,
    text: String = "",
    iconName: ImageVector,
    elevation: Dp = 0.dp,
    backgroundColor: Color = Color.Transparent,
    iconColor: Color = MaterialTheme.colors.onPrimary,
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
    textColor: Color = MaterialTheme.colors.onPrimary,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = { onClick() },
        modifier = modifier
            .height(40.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = elevation,
        backgroundColor = backgroundColor,
        border = border,
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = iconName,
                contentDescription = text,
                tint = iconColor
            )
            if (text.isNotEmpty()) {
                Spacer(modifier = Modifier.width(SpaceMini))
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1,
                    color = textColor,
                )
            }
        }
    }
}