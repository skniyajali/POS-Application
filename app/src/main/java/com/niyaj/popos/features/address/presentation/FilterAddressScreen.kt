package com.niyaj.popos.features.address.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.address.domain.util.FilterAddress
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.Teal200
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.components.FilterItem

@Composable
fun FilterAddressScreen(
    onClosePressed: () -> Unit,
    filterAddress: FilterAddress,
    onFilterChanged: (FilterAddress) -> Unit,
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
                color = if(filterAddress.sortType is SortType.Ascending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterAddress.sortType is SortType.Ascending,
                onSelected = {
                    onFilterChanged(
                        filterAddress.copy(SortType.Ascending)
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
                color = if(filterAddress.sortType is SortType.Descending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterAddress.sortType is SortType.Descending,
                onSelected = {
                    onFilterChanged(
                        filterAddress.copy(SortType.Descending)
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
            color = if(filterAddress is FilterAddress.ByAddressId) selectedColor else unselectedColor,
            itemSelected = filterAddress is FilterAddress.ByAddressId,
            onSelected = {
                onFilterChanged(
                    FilterAddress.ByAddressId(filterAddress.sortType)
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
            color = if(filterAddress is FilterAddress.ByAddressName) selectedColor else unselectedColor,
            itemSelected = filterAddress is FilterAddress.ByAddressName,
            onSelected = {
                onFilterChanged(
                    FilterAddress.ByAddressName(filterAddress.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Short Name",
            color = if(filterAddress is FilterAddress.ByShortName) selectedColor else unselectedColor,
            itemSelected = filterAddress is FilterAddress.ByShortName,
            onSelected = {
                onFilterChanged(
                    FilterAddress.ByShortName(filterAddress.sortType)
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
            color = if(filterAddress is FilterAddress.ByAddressDate) selectedColor else unselectedColor,
            itemSelected = filterAddress is FilterAddress.ByAddressDate,
            onSelected = {
                onFilterChanged(
                    FilterAddress.ByAddressDate(filterAddress.sortType)
                )
                onClosePressed()

            },
        )
    }

}