package com.niyaj.feature.employee.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toDate
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toSalaryDate
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Employee
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.event.UiState

/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmployeeDetails(
    modifier: Modifier = Modifier,
    employeeState: UiState<Employee>,
    employeeDetailsExpanded: Boolean = false,
    onClickEdit: () -> Unit = {},
    onExpanded: () -> Unit = {}
) {
    Card(
        onClick = onExpanded,
        modifier = modifier
            .testTag("EmployeeDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = employeeDetailsExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                TextWithIcon(
                    text = "Employee Details",
                    icon = Icons.Default.Person,
                    isTitle = true
                )
            },
            trailing = {
                IconButton(
                    onClick = onClickEdit
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Employee",
                        tint = MaterialTheme.colors.primary
                    )
                }
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = employeeState,
                    label = "EmployeeDetailsState"
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()
                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Employee details not found",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall)
                            ) {
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeeName),
                                    text = "Name - ${state.data.employeeName}",
                                    icon = Icons.Default.Person
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeePhone),
                                    text = "Phone - ${state.data.employeePhone}",
                                    icon = Icons.Default.PhoneAndroid
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeeSalary.toRupee),
                                    text = "Salary - ${state.data.employeeSalary.toRupee}",
                                    icon = Icons.Default.CurrencyRupee
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeeSalaryType.name),
                                    text = "Salary Type - ${state.data.employeeSalaryType.name}",
                                    icon = Icons.Default.Merge
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeePosition),
                                    text = "Position - ${state.data.employeePosition}",
                                    icon = Icons.Default.Approval
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeeType.name),
                                    text = "Type - ${state.data.employeeType.name}",
                                    icon = Icons.AutoMirrored.Filled.MergeType
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeeJoinedDate.toDate),
                                    text = "Joined Date : ${state.data.employeeJoinedDate.toSalaryDate}",
                                    icon = Icons.Default.CalendarToday
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    text = "Created At : ${state.data.createdAt.toFormattedDateAndTime}",
                                    icon = Icons.Default.AccessTime
                                )
                                state.data.updatedAt?.let {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    TextWithIcon(
                                        text = "Updated At : ${it.toFormattedDateAndTime}",
                                        icon = Icons.AutoMirrored.Filled.Login
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}