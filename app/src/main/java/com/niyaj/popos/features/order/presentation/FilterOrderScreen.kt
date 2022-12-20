package com.niyaj.popos.features.order.presentation

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
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.Teal200
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.components.FilterItem
import com.niyaj.popos.features.order.domain.util.FilterOrder
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun FilterOrderScreen(
    onClosePressed: () -> Unit,
    filterOrder: FilterOrder = FilterOrder.ByUpdatedDate(SortType.Descending),
    onFilterChanged: (FilterOrder) -> Unit = {},
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
                color = if(filterOrder.sortType is SortType.Ascending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterOrder.sortType is SortType.Ascending,
                onSelected = {
                    onFilterChanged(
                        filterOrder.copy(SortType.Ascending)
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
                color = if(filterOrder.sortType is SortType.Descending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterOrder.sortType is SortType.Descending,
                onSelected = {
                    onFilterChanged(
                        filterOrder.copy(SortType.Descending)
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
            filterName = "Sort By Date",
            color = if(filterOrder is FilterOrder.ByUpdatedDate) selectedColor else unselectedColor,
            itemSelected = filterOrder is FilterOrder.ByUpdatedDate,
            onSelected = {
                onFilterChanged(
                    FilterOrder.ByUpdatedDate(filterOrder.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))

        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Customer",
            color = if(filterOrder is FilterOrder.ByCustomerName) selectedColor else unselectedColor,
            itemSelected = filterOrder is FilterOrder.ByCustomerName,
            onSelected = {
                onFilterChanged(
                    FilterOrder.ByCustomerName(filterOrder.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Address",
            color = if(filterOrder is FilterOrder.ByCustomerAddress) selectedColor else unselectedColor,
            itemSelected = filterOrder is FilterOrder.ByCustomerAddress,
            onSelected = {
                onFilterChanged(
                    FilterOrder.ByCustomerAddress(filterOrder.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Order Status",
            color = if(filterOrder is FilterOrder.ByOrderStatus) selectedColor else unselectedColor,
            itemSelected = filterOrder is FilterOrder.ByOrderStatus,
            onSelected = {
                onFilterChanged(
                    FilterOrder.ByOrderStatus(filterOrder.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Order Type",
            color = if(filterOrder is FilterOrder.ByOrderType) selectedColor else unselectedColor,
            itemSelected = filterOrder is FilterOrder.ByOrderType,
            onSelected = {
                onFilterChanged(
                    FilterOrder.ByOrderType(filterOrder.sortType)
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
            color = if(filterOrder is FilterOrder.ByOrderPrice) selectedColor else unselectedColor,
            itemSelected = filterOrder is FilterOrder.ByOrderPrice,
            onSelected = {
                onFilterChanged(
                    FilterOrder.ByOrderPrice(filterOrder.sortType)
                )
                onClosePressed()

            },
        )

    }

}


