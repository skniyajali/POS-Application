package com.niyaj.feature.expenses.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TurnedInNot
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_TAG
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Expenses
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.NoteText

/**
 *
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ExpensesItem(
    modifier: Modifier = Modifier,
    categoryName: String,
    expense: Expenses,
    doesSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    borderStroke: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant)
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(EXPENSE_TAG.plus(expense.expensesId))
            .padding(SpaceSmall)
            .combinedClickable(
                onClick = {
                    onClick(expense.expensesId)
                },
                onLongClick = {
                    onLongClick(expense.expensesId)
                }
            ),
        shape = RoundedCornerShape(4.dp),
        border = if (doesSelected(expense.expensesId)) borderStroke else null,
        elevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth(),
                text = {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface
                    )
                },
                secondaryText = {
                    Text(
                        text = expense.expensesAmount.toRupee,
                        color = MaterialTheme.colors.onSurface
                    )
                },
                icon = {
                    CircularBox(
                        icon = Icons.Default.Person,
                        doesSelected = false,
                    )
                },
                trailing = {
                    NoteText(
                        text = expense.expensesDate.toPrettyDate(),
                        icon = Icons.Default.CalendarMonth
                    )
                },
            )

            if (expense.expensesRemarks.isNotEmpty()) {
                NoteText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.secondaryVariant)
                        .padding(SpaceSmall),
                    text = expense.expensesRemarks,
                    icon = Icons.Default.TurnedInNot,
                    color = MaterialTheme.colors.onSecondary
                )
            }
        }
    }
}