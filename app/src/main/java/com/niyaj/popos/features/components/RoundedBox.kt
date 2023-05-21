package com.niyaj.popos.features.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.common.ui.theme.SpaceMini

@Composable
fun RoundedBox(
    modifier: Modifier = Modifier,
    text: String = "",
    showIcon: Boolean = true,
    icon: ImageVector = Icons.Default.ExpandMore,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .border(BorderStroke(1.dp, Color.White), CutCornerShape(10))
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini)
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if(showIcon){
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Choose Date",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(SpaceMini))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.width(SpaceMini))
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}