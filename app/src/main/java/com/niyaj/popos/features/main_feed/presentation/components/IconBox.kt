package com.niyaj.popos.features.main_feed.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall

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
//            .width(148.dp)
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
            horizontalArrangement = Arrangement.Center,
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