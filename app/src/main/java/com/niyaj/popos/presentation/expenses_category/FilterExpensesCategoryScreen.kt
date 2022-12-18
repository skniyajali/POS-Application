package com.niyaj.popos.presentation.expenses_category

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterExpensesCategory
import com.niyaj.popos.presentation.components.FilterItem
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.presentation.ui.theme.Teal200
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun FilterExpensesCategoryScreen(
    onClosePressed: () -> Unit,
    filterExpensesCategory: FilterExpensesCategory = FilterExpensesCategory.ByExpensesCategoryId(SortType.Ascending),
    onFilterChanged: (FilterExpensesCategory) -> Unit = {},
    selectedColor: Color = MaterialTheme.colors.secondary,
    unselectedColor: Color = Teal200,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        Spacer(modifier = Modifier.height(SpaceSmall))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterItem(
                modifier = Modifier
                    .weight(1.2f)
                    .height(40.dp),
                filterName = "Ascending",
                color = if(filterExpensesCategory.sortType is SortType.Ascending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterExpensesCategory.sortType is SortType.Ascending,
                onSelected = {
                    onFilterChanged(
                        filterExpensesCategory.copy(SortType.Ascending)
                    )
                    onClosePressed()
                },
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            FilterItem(
                modifier = Modifier
                    .weight(1.2f)
                    .height(40.dp),
                filterName = "Descending",
                color = if(filterExpensesCategory.sortType is SortType.Descending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterExpensesCategory.sortType is SortType.Descending,
                onSelected = {
                    onFilterChanged(
                        filterExpensesCategory.copy(SortType.Descending)
                    )
                    onClosePressed()
                },
            )
        }

        Spacer(modifier = Modifier.height(SpaceMedium))

        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By ID",
            color = if(filterExpensesCategory is FilterExpensesCategory.ByExpensesCategoryId) selectedColor else unselectedColor,
            itemSelected = filterExpensesCategory is FilterExpensesCategory.ByExpensesCategoryId,
            onSelected = {
                onFilterChanged(
                    FilterExpensesCategory.ByExpensesCategoryId(filterExpensesCategory.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Name",
            color = if(filterExpensesCategory is FilterExpensesCategory.ByExpensesCategoryName) selectedColor else unselectedColor,
            itemSelected = filterExpensesCategory is FilterExpensesCategory.ByExpensesCategoryName,
            onSelected = {
                onFilterChanged(
                    FilterExpensesCategory.ByExpensesCategoryName(filterExpensesCategory.sortType)
                )
                onClosePressed()

            },
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Date",
            color = if(filterExpensesCategory is FilterExpensesCategory.ByExpensesCategoryDate) selectedColor else unselectedColor,
            itemSelected = filterExpensesCategory is FilterExpensesCategory.ByExpensesCategoryDate,
            onSelected = {
                onFilterChanged(
                    FilterExpensesCategory.ByExpensesCategoryDate(filterExpensesCategory.sortType)
                )
                onClosePressed()

            },
        )
    }
}