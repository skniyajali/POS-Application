package com.niyaj.popos.features.reports.presentation.components

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
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.components.RoundedBox
import com.niyaj.popos.features.employee_salary.domain.util.SalaryCalculableDate
import com.niyaj.popos.utils.toYearAndMonth

@Composable
fun OrderTypeDropdown(
    text: String,
    onItemClick: (String) -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column {
        RoundedBox(
            text = text,
            showIcon = false,
            onClick = {
                menuExpanded = !menuExpanded
            },
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    onItemClick("")
                    menuExpanded = false
                }
            ) {
                Text(
                    text = "All",
                    style = MaterialTheme.typography.body2,
                )
            }
            DropdownMenuItem(
                onClick = {
                    onItemClick(CartOrderType.DineIn.orderType)
                    menuExpanded = false
                }
            ) {
                Text(
                    text = "DineIn",
                    style = MaterialTheme.typography.body2,
                )
            }
            DropdownMenuItem(
                onClick = {
                    onItemClick(CartOrderType.DineOut.orderType)
                    menuExpanded = false
                }
            ) {
                Text(
                    text = "DineOut",
                    style = MaterialTheme.typography.body2,
                )
            }
        }
    }
}


@Composable
fun SalaryDateDropdown(
    text: String,
    salaryDates: List<SalaryCalculableDate> = emptyList(),
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