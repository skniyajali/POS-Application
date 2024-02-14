package com.niyaj.feature.expenses.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Expenses
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithIcon

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImportExportExpensesBody(
    lazyListState: LazyListState,
    groupedExpenses: Map<String, List<Expenses>>,
    selectedExpenses: List<String>,
    expanded: Boolean,
    onExpandChanged: () -> Unit,
    onSelectExpense: (String) -> Unit,
    onClickSelectAll: () -> Unit,
    backgroundColor: Color = LightColor8
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        backgroundColor = backgroundColor,
    ) {
        StandardExpandable(
            onExpandChanged = {
                onExpandChanged()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = expanded,
            title = {
                TextWithIcon(
                    text = if (selectedExpenses.isNotEmpty()) "${selectedExpenses.size} Selected" else "Select Expenses",
                    icon = Icons.Default.Dns,
                    isTitle = true
                )
            },
            rowClickable = true,
            trailing = {
                IconButton(
                    onClick = onClickSelectAll
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Rule,
                        contentDescription = "Select All Customers"
                    )
                }
            },
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                ExpensesItems(
                    lazyListState = lazyListState,
                    groupedExpenses = groupedExpenses,
                    doesSelected = {
                        selectedExpenses.contains(it)
                    },
                    onClick = onSelectExpense,
                    onLongClick = onSelectExpense,
                    headerColor = backgroundColor,
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(SpaceMedium))
}