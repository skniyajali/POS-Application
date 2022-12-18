package com.niyaj.popos.presentation.report.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.niyaj.popos.domain.model.SalaryCalculableDate
import com.niyaj.popos.domain.util.CartOrderType
import com.niyaj.popos.presentation.components.RoundedBox
import com.niyaj.popos.util.toYearAndMonth

@Composable
fun OrderTypeDropdown(
    text: String,
    onItemClick: (String) -> Unit = {}
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