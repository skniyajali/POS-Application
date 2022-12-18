package com.niyaj.popos.presentation.main_feed.components.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.niyaj.popos.presentation.ui.theme.IconSizeLarge
import com.niyaj.popos.presentation.ui.theme.SpaceSmall

@Composable
fun OrderIdSection() {
    Text(text = "Order-ID", style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold)

    Spacer(modifier = Modifier.height(SpaceSmall))

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
        .border(BorderStroke(1.dp, Color.Red), RoundedCornerShape(10)),) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "#123456", style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold)
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(
                IconSizeLarge)) {
                Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null)
            }
        }
    }
}
