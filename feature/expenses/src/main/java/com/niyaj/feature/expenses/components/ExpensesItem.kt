package com.niyaj.feature.expenses.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_TAG
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Expenses
import com.niyaj.ui.components.NoteText

/**
 *
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpensesItem(
    expense: Expenses,
    doesSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(EXPENSE_TAG.plus(expense.expensesId))
            .combinedClickable(
                onClick = {
                    onClick(expense.expensesId)
                },
                onLongClick = {
                    onLongClick(expense.expensesId)
                }
            ),
        shape = RoundedCornerShape(4.dp),
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.onSurface,
        border = if (doesSelected(expense.expensesId))
            BorderStroke(1.dp, MaterialTheme.colors.primary)
        else null,
        elevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = SpaceSmall)
                .padding(SpaceSmall)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = expense.expensesCategory?.expensesCategoryName!!,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                if (expense.expensesRemarks.isNotEmpty()) {
                    NoteText(text = expense.expensesRemarks)
                }
                Text(
                    text = expense.expensesAmount.toRupee,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}