package com.niyaj.popos.presentation.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterExpenses
import com.niyaj.popos.presentation.components.FilterItem
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.presentation.ui.theme.Teal200

@Composable
fun FilterExpensesScreen(
    onClosePressed: () -> Unit,
    filterExpenses: FilterExpenses = FilterExpenses.ByExpensesId(SortType.Ascending),
    onFilterChanged: (FilterExpenses) -> Unit = {},
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
                color = if(filterExpenses.sortType is SortType.Ascending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterExpenses.sortType is SortType.Ascending,
                onSelected = {
                    onFilterChanged(
                        filterExpenses.copy(SortType.Ascending)
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
                color = if(filterExpenses.sortType is SortType.Descending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterExpenses.sortType is SortType.Descending,
                onSelected = {
                    onFilterChanged(
                        filterExpenses.copy(SortType.Descending)
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
            color = if(filterExpenses is FilterExpenses.ByExpensesId) selectedColor else unselectedColor,
            itemSelected = filterExpenses is FilterExpenses.ByExpensesId,
            onSelected = {
                onFilterChanged(
                    FilterExpenses.ByExpensesId(filterExpenses.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Category",
            color = if(filterExpenses is FilterExpenses.ByExpensesCategory) selectedColor else unselectedColor,
            itemSelected = filterExpenses is FilterExpenses.ByExpensesCategory,
            onSelected = {
                onFilterChanged(
                    FilterExpenses.ByExpensesCategory(filterExpenses.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Price",
            color = if(filterExpenses is FilterExpenses.ByExpensesPrice) selectedColor else unselectedColor,
            itemSelected = filterExpenses is FilterExpenses.ByExpensesPrice,
            onSelected = {
                onFilterChanged(
                    FilterExpenses.ByExpensesPrice(filterExpenses.sortType)
                )
                onClosePressed()

            },
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Remarks",
            color = if(filterExpenses is FilterExpenses.ByExpensesRemarks) selectedColor else unselectedColor,
            itemSelected = filterExpenses is FilterExpenses.ByExpensesRemarks,
            onSelected = {
                onFilterChanged(
                    FilterExpenses.ByExpensesRemarks(filterExpenses.sortType)
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
            color = if(filterExpenses is FilterExpenses.ByExpensesDate) selectedColor else unselectedColor,
            itemSelected = filterExpenses is FilterExpenses.ByExpensesDate,
            onSelected = {
                onFilterChanged(
                    FilterExpenses.ByExpensesDate(filterExpenses.sortType)
                )
                onClosePressed()

            },
        )
    }
}