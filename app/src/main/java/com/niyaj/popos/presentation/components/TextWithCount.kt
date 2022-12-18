package com.niyaj.popos.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.popos.presentation.ui.theme.SpaceSmall

@Composable
fun TextWithCount(
    modifier: Modifier = Modifier,
    text: String,
    leadingIcon: ImageVector? = null,
    count: Int,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {
                    onClick()
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextWithIcon(
            text = text,
            icon = leadingIcon,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colors.secondaryVariant)
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSecondary,
                modifier = Modifier
            )
        }
    }
}