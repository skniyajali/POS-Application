package com.niyaj.feature.expenses_category.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.ExpensesCategory
import com.niyaj.ui.components.CircularBox

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpensesCategoryData(
    modifier: Modifier = Modifier,
    category: ExpensesCategory,
    doesSelected: (String) -> Boolean,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    borderStroke: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant)
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceMini)
            .combinedClickable(
                onClick = {
                    onClick(category.expensesCategoryId)
                },
                onLongClick = {
                    onLongClick(category.expensesCategoryId)
                },
            ),
        elevation = 2.dp,
        border = if(doesSelected(category.expensesCategoryId)) borderStroke else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularBox(
                icon = Icons.Default.Category,
                doesSelected = doesSelected(category.expensesCategoryId),
                text = category.expensesCategoryName,
                backgroundColor = MaterialTheme.colors.background
            )

            Text(
                text = category.expensesCategoryName,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
