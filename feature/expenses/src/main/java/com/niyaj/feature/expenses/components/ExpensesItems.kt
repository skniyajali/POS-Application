package com.niyaj.feature.expenses.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.ExpenseTestTags
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.IconSizeSmall
import com.niyaj.designsystem.theme.LightColor16
import com.niyaj.designsystem.theme.PurpleHaze
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Expenses
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.NoteText

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun GroupedExpensesData(
    modifier: Modifier = Modifier,
    categoryName: String,
    items: List<Expenses>,
    doesSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant)
) = trace("GroupedExpensesData") {
    val item = items.first()
    val totalAmount = items.sumOf { it.expensesAmount.toInt() }.toString()
    val notes = items.map { it.expensesRemarks }.filter { it.isNotEmpty() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .background(PurpleHaze),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(PurpleHaze),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ListItem(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface),
                text = {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.body1
                    )
                },
                secondaryText = {
                    Text(text = totalAmount.toRupee)
                },
                icon = {
                    CircularBox(
                        icon = Icons.Default.Person,
                        doesSelected = false,
                        text = categoryName
                    )
                },
                trailing = {
                    NoteText(
                        text = item.expensesDate.toPrettyDate(),
                        icon = Icons.Default.CalendarMonth
                    )
                },
            )

            Spacer(modifier = Modifier.height(SpaceMini))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall)
            ) {
                items.forEach { expense ->
                    val borderStroke = if (doesSelected(expense.expensesId)) border else null

                    Card(
                        modifier = modifier
                            .testTag(ExpenseTestTags.EXPENSE_TAG.plus(expense.expensesId))
                            .then(borderStroke?.let {
                                Modifier.border(it, RoundedCornerShape(SpaceMini))
                            } ?: Modifier)
                            .clip(RoundedCornerShape(SpaceMini))
                            .combinedClickable(
                                onClick = {
                                    onClick(expense.expensesId)
                                },
                                onLongClick = {
                                    onLongClick(expense.expensesId)
                                },
                            ),
                        shape = RoundedCornerShape(SpaceMini),
                        elevation = 2.dp,
                        backgroundColor = LightColor16
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(SpaceSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = if (doesSelected(expense.expensesId))
                                    Icons.Default.Check else Icons.Default.CurrencyRupee,
                                contentDescription = null,
                                modifier = Modifier.size(IconSizeSmall)
                            )

                            Spacer(modifier = Modifier.width(SpaceMini))

                            Text(
                                text = expense.expensesAmount,
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(SpaceSmall))
                }
            }

            Spacer(modifier = Modifier.height(SpaceMini))

            if (notes.isNotEmpty()) {
                Divider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceMini))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                ) {
                    notes.forEach { note ->
                        NoteText(
                            text = note,
                            icon = Icons.AutoMirrored.Filled.StickyNote2,
                            color = MaterialTheme.colors.error
                        )
                        Spacer(modifier = Modifier.height(SpaceMini))
                    }
                }
            }
        }
    }
}