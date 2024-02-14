package com.niyaj.feature.reports.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Reports
import com.niyaj.ui.components.ReportBox
import com.niyaj.ui.components.StandardButtonFW

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportBoxData(
    report: Reports,
    onOrderClick: () -> Unit,
    onExpensesClick: () -> Unit,
    onGenerateReport: () -> Unit
) {
    val totalAmount = report.expensesAmount
        .plus(report.dineInSalesAmount)
        .plus(report.dineOutSalesAmount)
        .toString()

    FlowRow(
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.Center
    ) {
        ReportBox(
            title = "DineIn Sales",
            amount = report.dineInSalesAmount.toString(),
            icon = Icons.Default.RamenDining,
            onClick = onOrderClick
        )

        ReportBox(
            title = "DineOut Sales",
            amount = report.dineOutSalesAmount.toString(),
            icon = Icons.Default.DeliveryDining,
            onClick = onOrderClick
        )

        ReportBox(
            title = "Expenses",
            amount = report.expensesAmount.toString(),
            icon = Icons.Default.Receipt,
            onClick = onExpensesClick
        )

        ReportBox(
            title = "Total Amount",
            amount = totalAmount,
            icon = Icons.Default.Money,
            enabled = false,
            onClick = {}
        )
    }

    Spacer(modifier = Modifier.height(SpaceSmall))

    StandardButtonFW(
        modifier = Modifier.fillMaxWidth(),
        text = "Re-Generate Report",
        icon = Icons.Default.Sync,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.secondaryVariant
        ),
        onClick = onGenerateReport
    )
}