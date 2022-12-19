package com.niyaj.popos.realm.employee.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.employee.domain.util.FilterEmployee
import com.niyaj.popos.presentation.components.FilterItem
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.presentation.ui.theme.Teal200

@Composable
fun FilterEmployeeScreen(
    onClosePressed: () -> Unit,
    filterEmployee: FilterEmployee = FilterEmployee.ByEmployeeId(SortType.Ascending),
    onFilterChanged: (FilterEmployee) -> Unit = {},
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
                color = if(filterEmployee.sortType is SortType.Ascending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterEmployee.sortType is SortType.Ascending,
                onSelected = {
                    onFilterChanged(
                        filterEmployee.copy(SortType.Ascending)
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
                color = if(filterEmployee.sortType is SortType.Descending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterEmployee.sortType is SortType.Descending,
                onSelected = {
                    onFilterChanged(
                        filterEmployee.copy(SortType.Descending)
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
            color = if(filterEmployee is FilterEmployee.ByEmployeeId) selectedColor else unselectedColor,
            itemSelected = filterEmployee is FilterEmployee.ByEmployeeId,
            onSelected = {
                onFilterChanged(
                    FilterEmployee.ByEmployeeId(filterEmployee.sortType)
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
            color = if(filterEmployee is FilterEmployee.ByEmployeeName) selectedColor else unselectedColor,
            itemSelected = filterEmployee is FilterEmployee.ByEmployeeName,
            onSelected = {
                onFilterChanged(
                    FilterEmployee.ByEmployeeName(filterEmployee.sortType)
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
            color = if(filterEmployee is FilterEmployee.ByEmployeePhone) selectedColor else unselectedColor,
            itemSelected = filterEmployee is FilterEmployee.ByEmployeePhone,
            onSelected = {
                onFilterChanged(
                    FilterEmployee.ByEmployeePhone(filterEmployee.sortType)
                )
                onClosePressed()

            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Salary",
            color = if(filterEmployee is FilterEmployee.ByEmployeeSalary) selectedColor else unselectedColor,
            itemSelected = filterEmployee is FilterEmployee.ByEmployeeSalary,
            onSelected = {
                onFilterChanged(
                    FilterEmployee.ByEmployeeSalary(filterEmployee.sortType)
                )
                onClosePressed()

            },
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Salary Type",
            color = if(filterEmployee is FilterEmployee.ByEmployeeSalaryType) selectedColor else unselectedColor,
            itemSelected = filterEmployee is FilterEmployee.ByEmployeeSalaryType,
            onSelected = {
                onFilterChanged(
                    FilterEmployee.ByEmployeeSalaryType(filterEmployee.sortType)
                )
                onClosePressed()

            },
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Position",
            color = if(filterEmployee is FilterEmployee.ByEmployeePosition) selectedColor else unselectedColor,
            itemSelected = filterEmployee is FilterEmployee.ByEmployeePosition,
            onSelected = {
                onFilterChanged(
                    FilterEmployee.ByEmployeePosition(filterEmployee.sortType)
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
            color = if(filterEmployee is FilterEmployee.ByEmployeeDate) selectedColor else unselectedColor,
            itemSelected = filterEmployee is FilterEmployee.ByEmployeeDate,
            onSelected = {
                onFilterChanged(
                    FilterEmployee.ByEmployeeDate(filterEmployee.sortType)
                )
                onClosePressed()

            },
        )
    }
}