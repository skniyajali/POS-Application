package com.niyaj.feature.employee.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.ui.components.IconBox
import com.niyaj.ui.components.StandardOutlinedChip


@Composable
fun EmployeePayment(
    modifier: Modifier = Modifier,
    payment: Payment,
) {
    Row(
        modifier = modifier
            .testTag(payment.employee?.employeeName.plus(payment.paymentId))
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = payment.paymentAmount.toRupee,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.8F)
        )

        Text(
            text = payment.paymentDate.toBarDate,
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
                text = payment.paymentMode.name,
                icon = when (payment.paymentMode) {
                    PaymentMode.Cash -> Icons.Default.Money
                    PaymentMode.Online -> Icons.Default.AccountBalance
                    else -> Icons.Default.Payments
                },
                selected = false,
            )

            Spacer(modifier = Modifier.width(SpaceSmall))

            StandardOutlinedChip(text = payment.paymentType.name)
        }
    }
}