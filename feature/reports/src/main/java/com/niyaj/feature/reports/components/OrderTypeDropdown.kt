package com.niyaj.feature.reports.components

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
import com.niyaj.model.OrderType
import com.niyaj.ui.components.RoundedBox

@Composable
fun OrderTypeDropdown(
    orderType: OrderType?,
    onItemClick: (OrderType?) -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column {
        RoundedBox(
            text = orderType?.name ?: "All",
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
                    onItemClick(null)
                    menuExpanded = false
                }
            ) {
                Text(
                    text = "All",
                    style = MaterialTheme.typography.body2,
                )
            }

            OrderType.entries.forEach {
                DropdownMenuItem(
                    onClick = {
                        onItemClick(it)
                        menuExpanded = false
                    }
                ) {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
    }
}