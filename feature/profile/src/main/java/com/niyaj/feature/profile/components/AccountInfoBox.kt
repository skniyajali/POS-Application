package com.niyaj.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.niyaj.designsystem.theme.LightColor7
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall

@Composable
fun AccountInfoBox(
    modifier : Modifier = Modifier,
    title: String,
    icon : ImageVector,
    value: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1.5f)
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.Black
            )
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Normal,
            )
        }

        Box(
            modifier = Modifier
                .weight(1.5f)
                .background(LightColor7, RoundedCornerShape(SpaceMini)),
            contentAlignment = Alignment.CenterEnd,
        ){
            Text(
                text = value,
                style = MaterialTheme.typography.body2,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(SpaceSmall)
            )
        }
    }
}