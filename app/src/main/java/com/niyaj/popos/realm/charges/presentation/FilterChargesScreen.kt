package com.niyaj.popos.realm.charges.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.charges.domain.util.FilterCharges
import com.niyaj.popos.presentation.components.FilterItem
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.presentation.ui.theme.Teal200

@Composable
fun FilterChargesScreen(
    onClosePressed: () -> Unit,
    filterCharges: FilterCharges = FilterCharges.ByChargesId(SortType.Descending),
    onFilterChanged: (FilterCharges) -> Unit = {},
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
                color = if(filterCharges.sortType is SortType.Ascending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterCharges.sortType is SortType.Ascending,
                onSelected = {
                    onFilterChanged(
                        filterCharges.copy(SortType.Ascending)
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
                color = if(filterCharges.sortType is SortType.Descending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterCharges.sortType is SortType.Descending,
                onSelected = {
                    onFilterChanged(
                        filterCharges.copy(SortType.Descending)
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
            color = if(filterCharges is FilterCharges.ByChargesId) selectedColor else unselectedColor,
            itemSelected = filterCharges is FilterCharges.ByChargesId,
            onSelected = {
                onFilterChanged(
                    FilterCharges.ByChargesId(filterCharges.sortType)
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
            color = if(filterCharges is FilterCharges.ByChargesName) selectedColor else unselectedColor,
            itemSelected = filterCharges is FilterCharges.ByChargesName,
            onSelected = {
                onFilterChanged(
                    FilterCharges.ByChargesName(filterCharges.sortType)
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
            color = if(filterCharges is FilterCharges.ByChargesPrice) selectedColor else unselectedColor,
            itemSelected = filterCharges is FilterCharges.ByChargesPrice,
            onSelected = {
                onFilterChanged(
                    FilterCharges.ByChargesPrice(filterCharges.sortType)
                )
                onClosePressed()

            },
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Applied",
            color = if(filterCharges is FilterCharges.ByChargesApplicable) selectedColor else unselectedColor,
            itemSelected = filterCharges is FilterCharges.ByChargesApplicable,
            onSelected = {
                onFilterChanged(
                    FilterCharges.ByChargesApplicable(filterCharges.sortType)
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
            color = if(filterCharges is FilterCharges.ByChargesDate) selectedColor else unselectedColor,
            itemSelected = filterCharges is FilterCharges.ByChargesDate,
            onSelected = {
                onFilterChanged(
                    FilterCharges.ByChargesDate(filterCharges.sortType)
                )
                onClosePressed()
            },
        )
    }

}