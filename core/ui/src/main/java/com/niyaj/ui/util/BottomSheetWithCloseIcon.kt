package com.niyaj.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.Constants.STANDARD_BOTTOM_SHEET
import com.niyaj.common.utils.Constants.STANDARD_BOTTOM_SHEET_CLOSE_BTN
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall

/**
 * Bottom sheet with close icon
 * @param modifier [Modifier]
 * @param closeBtnModifier [Modifier]
 * @param text [String]
 * @param icon [ImageVector]
 * @param color [Color]
 * @param onClosePressed [Unit]
 * @param content @Composable [Unit]
 * @author Sk Niyaj Ali
 */
@Composable
fun BottomSheetWithCloseDialog(
    modifier: Modifier = Modifier,
    closeBtnModifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    color: Color = MaterialTheme.colors.onSurface,
    onClosePressed: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .testTag(STANDARD_BOTTOM_SHEET)
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightColor6)
                .padding(SpaceMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon?.let {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        tint = color,
                    )
                    
                    Spacer(modifier = Modifier.width(SpaceMini))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.h6,
                    color = color,
                )
            }

            IconButton(
                onClick = onClosePressed,
                modifier = closeBtnModifier
                    .testTag(STANDARD_BOTTOM_SHEET_CLOSE_BTN)
                    .size(29.dp)
            ) {
                Icon(
                    Icons.Filled.Close,
                    tint = color,
                    contentDescription = STANDARD_BOTTOM_SHEET_CLOSE_BTN
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(SpaceSmall)
        ) {
            content()
        }

        Spacer(modifier = Modifier.height(SpaceMini))
    }
}