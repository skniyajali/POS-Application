package com.niyaj.popos.presentation.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.popos.presentation.ui.theme.IconSizeLarge
import com.niyaj.popos.presentation.ui.theme.SpaceSmall

@Composable
fun SelectedOrder(
    text: String = "",
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(35.dp)
            .border(BorderStroke(1.dp, Color.White), CutCornerShape(10))
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(onClick = { onClick() }, modifier = Modifier.size(IconSizeLarge)) {
                Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null)
            }
        }
    }
}