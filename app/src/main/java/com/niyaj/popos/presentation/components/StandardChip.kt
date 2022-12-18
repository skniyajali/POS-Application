package com.niyaj.popos.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.popos.presentation.ui.theme.IconSizeSmall
import com.niyaj.popos.presentation.ui.theme.SpaceMini
import com.niyaj.popos.presentation.ui.theme.SpaceSmall

@Composable
fun StandardChip(
    modifier: Modifier = Modifier,
    text: String = "",
    secondaryText: String? = null,
    isToggleable: Boolean = true,
    isSelected: Boolean = false,
    selectedColor: Color = MaterialTheme.colors.secondary,
    dissectedColor: Color = MaterialTheme.colors.onSecondary,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(if (isSelected) selectedColor else dissectedColor)
            .border(if (isSelected) 0.dp else 1.dp, selectedColor, RoundedCornerShape(2.dp))
            .toggleable(isToggleable) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if(isSelected){
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "$text added",
                    tint = dissectedColor,
                    modifier = Modifier.size(IconSizeSmall)
                )

                Spacer(modifier = Modifier.width(SpaceSmall))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.overline,
                textAlign = TextAlign.Center,
                color = if (isSelected) dissectedColor else selectedColor,
            )

            if(!secondaryText.isNullOrEmpty() && text.startsWith("Cold")){
                Text(
                    text = " Rs. $secondaryText",
                    style = MaterialTheme.typography.overline,
                    textAlign = TextAlign.Center,
                    color = if (isSelected) dissectedColor else selectedColor,
                )
            }
        }
    }
}


@Composable
fun PaymentStatusChip(
    modifier: Modifier = Modifier,
    text: String = "",
    isSelected: Boolean = false,
    selectedColor: Color = MaterialTheme.colors.secondary,
    dissectedColor: Color = MaterialTheme.colors.secondaryVariant,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(if (isSelected) selectedColor else dissectedColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if(isSelected){
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = text,
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.size(IconSizeSmall)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = text,
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.size(IconSizeSmall)
                )
            }

            Spacer(modifier = Modifier.width(SpaceSmall))


            Text(
                text = text,
                style = MaterialTheme.typography.overline,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onPrimary,
            )
        }
    }
}