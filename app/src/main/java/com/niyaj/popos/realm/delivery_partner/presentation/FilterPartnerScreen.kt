package com.niyaj.popos.realm.delivery_partner.presentation

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
import com.niyaj.popos.realm.delivery_partner.domain.util.FilterPartner

@Composable
fun FilterPartnerScreen(
    onClosePressed: () -> Unit,
    filterPartner: FilterPartner = FilterPartner.ByPartnerId(SortType.Ascending),
    onFilterChanged: (FilterPartner) -> Unit = {},
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
                color = if(filterPartner.sortType is SortType.Ascending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterPartner.sortType is SortType.Ascending,
                onSelected = {
                    onFilterChanged(
                        filterPartner.copy(SortType.Ascending)
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
                color = if(filterPartner.sortType is SortType.Descending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterPartner.sortType is SortType.Descending,
                onSelected = {
                    onFilterChanged(
                        filterPartner.copy(SortType.Descending)
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
            color = if(filterPartner is FilterPartner.ByPartnerId) selectedColor else unselectedColor,
            itemSelected = filterPartner is FilterPartner.ByPartnerId,
            onSelected = {
                onFilterChanged(
                    FilterPartner.ByPartnerId(filterPartner.sortType)
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
            color = if(filterPartner is FilterPartner.ByPartnerName) selectedColor else unselectedColor,
            itemSelected = filterPartner is FilterPartner.ByPartnerName,
            onSelected = {
                onFilterChanged(
                    FilterPartner.ByPartnerName(filterPartner.sortType)
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
            color = if(filterPartner is FilterPartner.ByPartnerPhone) selectedColor else unselectedColor,
            itemSelected = filterPartner is FilterPartner.ByPartnerPhone,
            onSelected = {
                onFilterChanged(
                    FilterPartner.ByPartnerPhone(filterPartner.sortType)
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
            color = if(filterPartner is FilterPartner.ByPartnerEmail) selectedColor else unselectedColor,
            itemSelected = filterPartner is FilterPartner.ByPartnerEmail,
            onSelected = {
                onFilterChanged(
                    FilterPartner.ByPartnerEmail(filterPartner.sortType)
                )
                onClosePressed()

            },
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Status",
            color = if(filterPartner is FilterPartner.ByPartnerStatus) selectedColor else unselectedColor,
            itemSelected = filterPartner is FilterPartner.ByPartnerStatus,
            onSelected = {
                onFilterChanged(
                    FilterPartner.ByPartnerStatus(filterPartner.sortType)
                )
                onClosePressed()

            },
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Partner Type",
            color = if(filterPartner is FilterPartner.ByPartnerType) selectedColor else unselectedColor,
            itemSelected = filterPartner is FilterPartner.ByPartnerType,
            onSelected = {
                onFilterChanged(
                    FilterPartner.ByPartnerType(filterPartner.sortType)
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
            color = if(filterPartner is FilterPartner.ByPartnerDate) selectedColor else unselectedColor,
            itemSelected = filterPartner is FilterPartner.ByPartnerDate,
            onSelected = {
                onFilterChanged(
                    FilterPartner.ByPartnerDate(filterPartner.sortType)
                )
                onClosePressed()

            },
        )
    }
}