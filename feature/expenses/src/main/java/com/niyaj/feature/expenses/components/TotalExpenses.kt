package com.niyaj.feature.expenses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.common.tags.EmployeeTestTags
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.NoteText
import com.niyaj.ui.components.RoundedBox

/**
 *
 */
@Composable
fun TotalExpenses(
    selectedDate: String,
    totalAmount: String,
    totalPayment: Int,
    onClickDatePicker: () -> Unit
) {
    Card(
        modifier = Modifier
            .testTag("CalculateTotalExpenses")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Total Expenses",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                RoundedBox(
                    text = selectedDate,
                    showIcon = true,
                    onClick = onClickDatePicker,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = totalAmount.ifEmpty { "0" }.toRupee,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag(EmployeeTestTags.REMAINING_AMOUNT_TEXT)
                )

                Text(
                    text = "Total $totalPayment Payment",
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.End
                )
            }

            if (totalPayment == 0) {
                Spacer(modifier = Modifier.height(SpaceMini))
                NoteText(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "You haven't paid any expenses yet."
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}