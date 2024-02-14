package com.niyaj.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.Olive
import com.niyaj.designsystem.theme.TextGray

@Composable
fun PhoneNoCountBox(
    modifier : Modifier = Modifier,
    count: Int = 0,
    totalCount: Int = 10,
    backgroundColor: Color = Color.Transparent,
    color: Color = TextGray,
    errorColor: Color = Olive,
) {
    val countColor = if (count <= 10) color else errorColor
    val textColor = if (count >= 10) color else errorColor

    AnimatedVisibility(
        visible = count != 0,
        enter = fadeIn(),
        exit = fadeOut(),
        label = "Phone No Count Box",
    ) {
        Card(
            modifier = modifier.background(backgroundColor),
            shape = RoundedCornerShape(2.dp),
            elevation = 0.dp,
        ) {
            Row(
                modifier = Modifier.padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.caption,
                    color = countColor,
                )
                Text(
                    text = "/",
                    fontFamily = FontFamily.Cursive,
                    color = color,
                )
                Text(
                    text = totalCount.toString(),
                    style = MaterialTheme.typography.caption,
                    color = textColor,
                )
            }
        }
    }
}