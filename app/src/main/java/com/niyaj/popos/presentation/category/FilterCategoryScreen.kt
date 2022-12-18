package com.niyaj.popos.presentation.category

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterCategory
import com.niyaj.popos.presentation.components.FilterItem
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.presentation.ui.theme.Teal200

@Composable
fun FilterCategoryScreen(
    onClosePressed: () -> Unit,
    filterCategory: FilterCategory = FilterCategory.ByCategoryId(SortType.Ascending),
    onFilterChanged: (FilterCategory) -> Unit = {},
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
                color = if(filterCategory.sortType is SortType.Ascending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterCategory.sortType is SortType.Ascending,
                onSelected = {
                    onFilterChanged(
                        filterCategory.copy(SortType.Ascending)
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
                color = if(filterCategory.sortType is SortType.Descending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterCategory.sortType is SortType.Descending,
                onSelected = {
                    onFilterChanged(
                        filterCategory.copy(SortType.Descending)
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
            color = if(filterCategory is FilterCategory.ByCategoryId) selectedColor else unselectedColor,
            itemSelected = filterCategory is FilterCategory.ByCategoryId,
            onSelected = {
                onFilterChanged(
                    FilterCategory.ByCategoryId(filterCategory.sortType)
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
            color = if(filterCategory is FilterCategory.ByCategoryName) selectedColor else unselectedColor,
            itemSelected = filterCategory is FilterCategory.ByCategoryName,
            onSelected = {
                onFilterChanged(
                    FilterCategory.ByCategoryName(filterCategory.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Availability",
            color = if(filterCategory is FilterCategory.ByCategoryAvailability) selectedColor else unselectedColor,
            itemSelected = filterCategory is FilterCategory.ByCategoryAvailability,
            onSelected = {
                onFilterChanged(
                    FilterCategory.ByCategoryAvailability(filterCategory.sortType)
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
            color = if(filterCategory is FilterCategory.ByCategoryDate) selectedColor else unselectedColor,
            itemSelected = filterCategory is FilterCategory.ByCategoryDate,
            onSelected = {
                onFilterChanged(
                    FilterCategory.ByCategoryDate(filterCategory.sortType)
                )
                onClosePressed()

            },
        )
    }
}