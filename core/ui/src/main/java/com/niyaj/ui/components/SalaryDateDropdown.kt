package com.niyaj.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.niyaj.common.utils.toYearAndMonth
import com.niyaj.model.EmployeeMonthlyDate

@Composable
fun SalaryDateDropdown(
    text: String,
    salaryDates: List<EmployeeMonthlyDate> = emptyList(),
    onDateClick: (Pair<String, String>) -> Unit = {},
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column {
        RoundedBox(
            text = text,
            showIcon = true,
            onClick = {
                menuExpanded = !menuExpanded
            },
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            salaryDates.forEach { date ->
                DropdownMenuItem(
                    onClick = {
                        onDateClick(Pair(date.startDate, date.endDate))
                        menuExpanded = false
                    }
                ) {
                    Text(
                        text = date.startDate.toYearAndMonth,
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
    }
}