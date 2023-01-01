package com.niyaj.popos.features.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.common.ui.theme.SpaceSmall

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

        CountBox(count = count.toString())
    }
}


@Composable
fun CountBox(
    modifier: Modifier = Modifier,
    count: String,
    backGroundColor: Color =  MaterialTheme.colors.secondaryVariant
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(backGroundColor)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSecondary,
            modifier = Modifier
        )
    }
}