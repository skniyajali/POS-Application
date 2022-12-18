package com.niyaj.popos.presentation.components.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.popos.presentation.ui.theme.BackgroundColor1
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall

@Composable
fun BottomSheetWithCloseDialog(
    modifier: Modifier = Modifier,
    text: String,
    onClosePressed: () -> Unit = {},
    closeButtonColor: Color = MaterialTheme.colors.onSurface,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundColor1)
                .padding(SpaceMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface
            )
            IconButton(
                onClick = onClosePressed,
                modifier = Modifier.size(29.dp)
            ) {
                Icon(
                    Icons.Filled.Close,
                    tint = closeButtonColor,
                    contentDescription = null
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(SpaceSmall)
        ) {
            content()
        }
    }
}