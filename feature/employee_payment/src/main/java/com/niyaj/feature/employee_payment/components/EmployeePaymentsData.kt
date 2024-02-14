package com.niyaj.feature.employee_payment.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.ui.components.IconBox
import com.niyaj.ui.components.StandardOutlinedChip


/**
 *
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmployeePaymentsData(
    employeeSalaries: List<Payment>,
    doesSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    borderStroke: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.primary)
) {
    employeeSalaries.forEachIndexed { index, item ->
        Card(
            modifier = Modifier
                .testTag(item.employee?.employeeName.plus(item.paymentAmount))
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onClick(item.paymentId)
                    },
                    onLongClick = {
                        onLongClick(item.paymentId)
                    },
                ),
            elevation = if (doesSelected(item.paymentId)) 2.dp else 0.dp,
            backgroundColor = if (doesSelected(item.paymentId)) LightColor6 else MaterialTheme.colors.surface,
            border = if (doesSelected(item.paymentId)) borderStroke else null,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.paymentAmount.toRupee,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(0.8F),
                )

                Text(
                    text = item.paymentDate.toBarDate,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.weight(0.8F),
                )

                Row(
                    modifier = Modifier.weight(1.4F),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    IconBox(
                        text = item.paymentMode.name,
                        icon = when (item.paymentMode) {
                            PaymentMode.Cash -> Icons.Default.Money
                            PaymentMode.Online -> Icons.Default.AccountBalance
                            else -> Icons.Default.Payments
                        },
                        selected = false,
                    )

                    Spacer(modifier = Modifier.width(SpaceSmall))

                    StandardOutlinedChip(text = item.paymentType.name)
                }

            }
        }

        if (index != employeeSalaries.size - 1) {
            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}