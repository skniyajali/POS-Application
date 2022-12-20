package com.niyaj.popos.features.product.presentation

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
import com.niyaj.popos.features.product.domain.util.FilterProduct
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun FilterProductScreen(
    onClosePressed: () -> Unit,
    filterProduct: FilterProduct = FilterProduct.ByProductId(SortType.Ascending),
    onFilterChanged: (FilterProduct) -> Unit = {},
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
                color = if(filterProduct.sortType is SortType.Ascending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterProduct.sortType is SortType.Ascending,
                onSelected = {
                    onFilterChanged(
                        filterProduct.copy(SortType.Ascending)
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
                color = if(filterProduct.sortType is SortType.Descending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterProduct.sortType is SortType.Descending,
                onSelected = {
                    onFilterChanged(
                        filterProduct.copy(SortType.Descending)
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
            color = if(filterProduct is FilterProduct.ByProductId) selectedColor else unselectedColor,
            itemSelected = filterProduct is FilterProduct.ByProductId,
            onSelected = {
                onFilterChanged(
                    FilterProduct.ByProductId(filterProduct.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceMedium))

        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Category ID",
            color = if(filterProduct is FilterProduct.ByCategoryId) selectedColor else unselectedColor,
            itemSelected = filterProduct is FilterProduct.ByCategoryId,
            onSelected = {
                onFilterChanged(
                    FilterProduct.ByCategoryId(filterProduct.sortType)
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
            color = if(filterProduct is FilterProduct.ByProductName) selectedColor else unselectedColor,
            itemSelected = filterProduct is FilterProduct.ByProductName,
            onSelected = {
                onFilterChanged(
                    FilterProduct.ByProductName(filterProduct.sortType)
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
            color = if(filterProduct is FilterProduct.ByProductPrice) selectedColor else unselectedColor,
            itemSelected = filterProduct is FilterProduct.ByProductPrice,
            onSelected = {
                onFilterChanged(
                    FilterProduct.ByProductPrice(filterProduct.sortType)
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
            color = if(filterProduct is FilterProduct.ByProductAvailability) selectedColor else unselectedColor,
            itemSelected = filterProduct is FilterProduct.ByProductAvailability,
            onSelected = {
                onFilterChanged(
                    FilterProduct.ByProductAvailability(filterProduct.sortType)
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
            color = if(filterProduct is FilterProduct.ByProductDate) selectedColor else unselectedColor,
            itemSelected = filterProduct is FilterProduct.ByProductDate,
            onSelected = {
                onFilterChanged(
                    FilterProduct.ByProductDate(filterProduct.sortType)
                )
                onClosePressed()

            },
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Quantity",
            color = if(filterProduct is FilterProduct.ByProductQuantity) selectedColor else unselectedColor,
            itemSelected = filterProduct is FilterProduct.ByProductQuantity,
            onSelected = {
                onFilterChanged(
                    FilterProduct.ByProductQuantity(filterProduct.sortType)
                )
                onClosePressed()

            },
        )
    }
}