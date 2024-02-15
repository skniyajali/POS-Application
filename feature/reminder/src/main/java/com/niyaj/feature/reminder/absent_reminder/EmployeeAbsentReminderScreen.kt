package com.niyaj.feature.reminder.absent_reminder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.ReminderTestTags
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.Constants.ABSENT_HOST
import com.niyaj.common.utils.Constants.ABSENT_HOST_SECURE
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.reminder.components.EmployeeSelectionBodyRow
import com.niyaj.feature.reminder.components.EmployeeSelectionFooter
import com.niyaj.feature.reminder.components.EmployeeSelectionHeader
import com.niyaj.feature.reminder.components.InfoCard
import com.niyaj.model.PaymentStatus
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.ItemNotFound
import com.niyaj.ui.components.StandardIconButton
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.components.TextWithBorderCount
import com.niyaj.ui.components.TopBarTitle
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.PaymentUiStatus
import com.niyaj.ui.util.Screens
import com.niyaj.ui.util.isScrolled
import com.niyaj.ui.util.toUiStatus
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

/**
 * Absent Reminder Screen
 * @author Sk Niyaj Ali
 */
@OptIn(ExperimentalFoundationApi::class)
@Destination(
    deepLinks = [
        DeepLink(uriPattern = ABSENT_HOST_SECURE),
        DeepLink(uriPattern = ABSENT_HOST)
    ]
)
@Composable
fun EmployeeAbsentReminderScreen(
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: AbsentReminderViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val listState = rememberLazyListState()

    val employees = viewModel.employees.collectAsStateWithLifecycle().value
    val groupedBy = remember(employees) {
        employees.sortedBy { it.paymentStatus }
            .groupBy { it.paymentStatus.toUiStatus() }
    }

    val selectedDate = viewModel.selectedDate.collectAsStateWithLifecycle().value

    val selectedEmployees = viewModel.selectedEmployees

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }
            }
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        showBottomBar = employees.isNotEmpty() && !listState.isScrolled,
        navActions = {
            StandardIconButton(
                onClick = {
                    navController.navigateUp()
                },
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close_icon),
                tint = MaterialTheme.colors.onPrimary,
            )
        },
        title = {
            TopBarTitle(
                text = "Mark Absent Employee",
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background)
                    .padding(
                        bottom = SpaceSmall,
                        top = SpaceMini,
                        start = SpaceMedium,
                        end = SpaceMedium
                    )
            ) {
                AnimatedVisibility(
                    visible = selectedEmployees.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    InfoCard(text = Constants.ABSENT_REMINDER_NOTE)
                }

                EmployeeSelectionFooter(
                    primaryText = "Mark As Absent",
                    onPrimaryClick = {
                        viewModel.onEvent(AbsentReminderEvent.MarkAsAbsent)
                    },
                    onSecondaryClick = {
                        navController.navigateUp()
                    }
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        },
    ) { paddingValues ->
        if (employees.isEmpty()) {
            ItemNotAvailable(
                modifier = Modifier.fillMaxWidth(),
                text = "Employees not found!",
                buttonText = "Create new employee",
                onClick = {
                    navController.navigate(Screens.ADD_EDIT_EMPLOYEE_SCREEN)
                }
            )
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall)
                    .padding(paddingValues),
                shape = RoundedCornerShape(0.dp),
                backgroundColor = LightColor6,
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = paddingValues
                ) {
                    item {
                        EmployeeSelectionHeader(
                            selectedDate = selectedDate,
                            selectionCount = selectedEmployees.size,
                            checked = selectedEmployees.isNotEmpty(),
                            onSelectDate = {
                                viewModel.onEvent(AbsentReminderEvent.SelectDate(it))
                            },
                            onCheckedChange = {
                                viewModel.onEvent(AbsentReminderEvent.SelectAllEmployee)
                            },
                        )
                    }

                    groupedBy.forEach { (paymentStatus, groupedReminderEmployee) ->
                        if (paymentStatus != PaymentUiStatus.NotPaid) {
                            stickyHeader {
                                TextWithBorderCount(
                                    modifier = Modifier,
                                    text = paymentStatus.status,
                                    leadingIcon = paymentStatus.icon,
                                    count = groupedReminderEmployee.size,
                                )
                            }
                        }

                        itemsIndexed(
                            items = groupedReminderEmployee,
                            key = { index, item ->
                                item.employee.employeeId.plus(index)
                            }
                        ) { index, reminderEmployee ->

                            val isEnabled = when (reminderEmployee.paymentStatus) {
                                PaymentStatus.NotPaid -> true
                                else -> false
                            }

                            EmployeeSelectionBodyRow(
                                primaryText = reminderEmployee.employee.employeeName,
                                secText = reminderEmployee.employee.employeePhone,
                                secIcon = Icons.Default.PhoneAndroid,
                                isSelected = selectedEmployees.contains(reminderEmployee.employee.employeeId),
                                paymentUiStatus = reminderEmployee.paymentStatus.toUiStatus(),
                                isEnabled = isEnabled,
                                onSelectEmployee = {
                                    viewModel.onEvent(
                                        AbsentReminderEvent.SelectEmployee(
                                            reminderEmployee.employee.employeeId
                                        )
                                    )
                                }
                            )

                            if (index != employees.size - 1) {
                                Divider(modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }

                    item {
                        ItemNotFound(
                            modifier = Modifier.padding(vertical = SpaceMedium),
                            text = ReminderTestTags.EMPLOYEE_NOT_FOUND,
                            buttonText = ReminderTestTags.CREATE_NEW_EMP,
                            onClick = {
                                navController.navigate(Screens.ADD_EDIT_EMPLOYEE_SCREEN)
                            }
                        )
                    }
                }
            }
        }
    }

}