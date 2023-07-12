package com.niyaj.popos.features.main_feed.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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