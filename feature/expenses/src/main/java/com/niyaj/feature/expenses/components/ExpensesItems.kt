package com.niyaj.feature.expenses.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Expenses
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.util.isScrolled

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpensesItems(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    groupedExpenses: Map<String, List<Expenses>>,
    doesSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    headerColor: Color = MaterialTheme.colors.onPrimary,
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
    ) {
        groupedExpenses.forEach { (date, expensesList) ->
            stickyHeader {
                TextWithCount(
                    modifier = Modifier
                        .background(if (lazyListState.isScrolled) headerColor else Color.Transparent)
                        .clip(RoundedCornerShape(if (lazyListState.isScrolled) 4.dp else 0.dp)),
                    text = date,
                    count = expensesList.count(),
                    onClick = {}
                )
            }

            itemsIndexed(
                items = expensesList,
                key = { index, item ->
                    item.expensesId.plus(index)
                }
            ) { index, expense ->
                ExpensesItem(
                    expense = expense,
                    doesSelected = doesSelected,
                    onClick = onClick,
                    onLongClick = onLongClick,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                if (index == expensesList.size - 1) {
                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }
        }
    }
}