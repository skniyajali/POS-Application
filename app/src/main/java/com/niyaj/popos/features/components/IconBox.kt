package com.niyaj.popos.features.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.common.ui.theme.IconSizeSmall
import com.niyaj.popos.features.common.ui.theme.LightColor8
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun IconBox(
    text: String,
    icon: ImageVector? = null,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    val borderStroke = if (selected) BorderStroke(1.dp, MaterialTheme.colors.primary) else BorderStroke(0.dp, Color.Transparent)
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