package com.niyaj.popos.realm.customer.presentation

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
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.presentation.components.FilterItem
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.presentation.ui.theme.Teal200
import com.niyaj.popos.realm.customer.domain.util.FilterCustomer

@Composable
fun FilterCustomerScreen(
    onClosePressed: () -> Unit,
    filterCustomer: FilterCustomer = FilterCustomer.ByCustomerId(SortType.Ascending),
    onFilterChanged: (FilterCustomer) -> Unit = {},
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
                color = if(filterCustomer.sortType is SortType.Ascending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterCustomer.sortType is SortType.Ascending,
                onSelected = {
                    onFilterChanged(
                        filterCustomer.copy(SortType.Ascending)
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
                color = if(filterCustomer.sortType is SortType.Descending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterCustomer.sortType is SortType.Descending,
                onSelected = {
                    onFilterChanged(
                        filterCustomer.copy(SortType.Descending)
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
            color = if(filterCustomer is FilterCustomer.ByCustomerId) selectedColor else unselectedColor,
            itemSelected = filterCustomer is FilterCustomer.ByCustomerId,
            onSelected = {
                onFilterChanged(
                    FilterCustomer.ByCustomerId(filterCustomer.sortType)
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
            color = if(filterCustomer is FilterCustomer.ByCustomerName) selectedColor else unselectedColor,
            itemSelected = filterCustomer is FilterCustomer.ByCustomerName,
            onSelected = {
                onFilterChanged(
                    FilterCustomer.ByCustomerName(filterCustomer.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Email",
            color = if(filterCustomer is FilterCustomer.ByCustomerEmail) selectedColor else unselectedColor,
            itemSelected = filterCustomer is FilterCustomer.ByCustomerEmail,
            onSelected = {
                onFilterChanged(
                    FilterCustomer.ByCustomerEmail(filterCustomer.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Phone Number",
            color = if(filterCustomer is FilterCustomer.ByCustomerPhone) selectedColor else unselectedColor,
            itemSelected = filterCustomer is FilterCustomer.ByCustomerPhone,
            onSelected = {
                onFilterChanged(
                    FilterCustomer.ByCustomerPhone(filterCustomer.sortType)
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
            color = if(filterCustomer is FilterCustomer.ByCustomerDate) selectedColor else unselectedColor,
            itemSelected = filterCustomer is FilterCustomer.ByCustomerDate,
            onSelected = {
                onFilterChanged(
                    FilterCustomer.ByCustomerDate(filterCustomer.sortType)
                )
                onClosePressed()

            },
        )
    }
}