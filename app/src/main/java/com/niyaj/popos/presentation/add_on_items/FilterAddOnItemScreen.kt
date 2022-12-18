package com.niyaj.popos.presentation.add_on_items

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterAddOnItem
import com.niyaj.popos.presentation.components.FilterItem
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.presentation.ui.theme.Teal200

@Composable
fun FilterAddOnItemScreen(
    onClosePressed: () -> Unit,
    filterAddOnItem: FilterAddOnItem,
    onFilterChanged: (FilterAddOnItem) -> Unit,
) {

    val selectedColor: Color = MaterialTheme.colors.secondary
    val unselectedColor: Color = Teal200

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
                color = if(filterAddOnItem.sortType is SortType.Ascending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterAddOnItem.sortType is SortType.Ascending,
                onSelected = {
                    onFilterChanged(filterAddOnItem.copy(SortType.Ascending))
                    onClosePressed()
                },
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            FilterItem(
                modifier = Modifier
                    .weight(1.2f)
                    .height(40.dp),
                filterName = "Descending",
                color = if(filterAddOnItem.sortType is SortType.Descending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterAddOnItem.sortType is SortType.Descending,
                onSelected = {
                    onFilterChanged(filterAddOnItem.copy(SortType.Descending))
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
            color = if(filterAddOnItem is FilterAddOnItem.ByAddOnItemId) selectedColor else unselectedColor,
            itemSelected = filterAddOnItem is FilterAddOnItem.ByAddOnItemId,
            onSelected = {
                onFilterChanged(FilterAddOnItem.ByAddOnItemId(filterAddOnItem.sortType))
                onClosePressed()
            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Name",
            color = if(filterAddOnItem is FilterAddOnItem.ByAddOnItemName) selectedColor else unselectedColor,
            itemSelected = filterAddOnItem is FilterAddOnItem.ByAddOnItemName,
            onSelected = {
                onFilterChanged(FilterAddOnItem.ByAddOnItemName(filterAddOnItem.sortType))
                onClosePressed()
            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Price",
            color = if(filterAddOnItem is FilterAddOnItem.ByAddOnItemPrice) selectedColor else unselectedColor,
            itemSelected = filterAddOnItem is FilterAddOnItem.ByAddOnItemPrice,
            onSelected = {
                onFilterChanged(FilterAddOnItem.ByAddOnItemPrice(filterAddOnItem.sortType))
                onClosePressed()
            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Date",
            color = if(filterAddOnItem is FilterAddOnItem.ByAddOnItemDate) selectedColor else unselectedColor,
            itemSelected = filterAddOnItem is FilterAddOnItem.ByAddOnItemDate,
            onSelected = {
                onFilterChanged(FilterAddOnItem.ByAddOnItemDate(filterAddOnItem.sortType))
                onClosePressed()
            },
        )
    }
}