package com.niyaj.feature.reminder.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person4
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.getCalculatedStartDate
import com.niyaj.common.utils.getEndTime
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.IconSizeMedium
import com.niyaj.designsystem.theme.Pewter
import com.niyaj.designsystem.theme.PoposPink300
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.Teal200
import com.niyaj.ui.components.PaymentStatusChip
import com.niyaj.ui.components.RoundedBox
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.components.TextWithTitle
import com.niyaj.ui.util.PaymentUiStatus
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState


@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = Pewter,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        backgroundColor = backgroundColor,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info Icon",
                tint = Teal200,
                modifier = Modifier
                    .size(IconSizeMedium)

            )

            Spacer(modifier = Modifier.width(SpaceMini))

            Text(
                text = text,
                style = MaterialTheme.typography.body1,
                maxLines = 2,
            )
        }
    }
}

@Composable
fun EmployeeSelectionHeader(
    modifier: Modifier = Modifier,
    selectedDate: String,
    selectionCount: Int = 0,
    checked: Boolean = false,
    onSelectDate: (String) -> Unit,
    onCheckedChange: () -> Unit,
    backgroundColor: Color = PoposPink300,
) {
    val dialogState = rememberMaterialDialogState()

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker(
            allowedDateValidator = { date ->
                date.toMilliSecond in getCalculatedStartDate("-7")..getEndTime
            }
        ) { date ->
            onSelectDate(date.toMilliSecond)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = backgroundColor,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMini),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        onCheckedChange()
                    }
                )

                Text(
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onCheckedChange()
                    },
                    text = if (selectionCount != 0) "$selectionCount Selected" else "Select All",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.body1,
                )
            }

            RoundedBox(
                text = selectedDate.toPrettyDate(),
                onClick = {
                    dialogState.show()
                }
            )
        }
    }
}

@Composable
fun EmployeeSelectionBodyRow(
    modifier: Modifier = Modifier,
    primaryText: String,
    secText: String,
    onSelectEmployee: () -> Unit = {},
    isSelected: Boolean = false,
    isEnabled: Boolean = true,
    paymentUiStatus: PaymentUiStatus? = null,
    secIcon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceMini)
            .clickable(
                enabled = isEnabled,
            ) {
                onSelectEmployee()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(if (paymentUiStatus != PaymentUiStatus.NotPaid) SpaceMedium else 0.dp)
        ) {
            if (paymentUiStatus == PaymentUiStatus.NotPaid) {
                Checkbox(
                    enabled = isEnabled,
                    checked = isSelected,
                    onCheckedChange = {
                        onSelectEmployee()
                    },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = MaterialTheme.colors.secondaryVariant,
                    )
                )
            }

            TextWithIcon(
                text = primaryText,
                isTitle = true,
                icon = Icons.Default.Person4
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (paymentUiStatus == PaymentUiStatus.NotPaid) {
                TextWithTitle(
                    modifier = Modifier,
                    text = secText,
                    icon = secIcon
                )
            }

            paymentUiStatus?.let {
                if (it != PaymentUiStatus.NotPaid) {
                    PaymentStatusChip(paymentStatus = it)
                }
            }
        }
    }
}

@Composable
fun EmployeeSelectionFooter(
    modifier: Modifier = Modifier,
    primaryText: String,
    primaryIcon: ImageVector? = Icons.Default.EventAvailable,
    secondaryText: String = "Cancel, Do It Later",
    secondaryIcon: ImageVector? = Icons.Default.HighlightOff,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(SpaceMedium))

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedButton(
            onClick = onSecondaryClick,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(ButtonSize),
            border = BorderStroke(1.dp, MaterialTheme.colors.error),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colors.error
            ),
            shape = RoundedCornerShape(SpaceMini),
        ) {
            secondaryIcon?.let {
                Icon(
                    imageVector = secondaryIcon,
                    contentDescription = secondaryText,
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
            }

            Text(text = secondaryText.uppercase())
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        Button(
            onClick = onPrimaryClick,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(ButtonSize),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondaryVariant
            ),
            shape = RoundedCornerShape(SpaceMini),
        ) {
            primaryIcon?.let {
                Icon(
                    imageVector = primaryIcon,
                    contentDescription = primaryText,
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
            }

            Text(
                primaryText.uppercase(),
                style = MaterialTheme.typography.button
            )
        }
    }

}