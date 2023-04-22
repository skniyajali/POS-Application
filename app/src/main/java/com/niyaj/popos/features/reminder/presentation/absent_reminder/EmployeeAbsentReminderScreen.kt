package com.niyaj.popos.features.reminder.presentation.absent_reminder

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.LightColor6
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.*
import com.niyaj.popos.features.reminder.domain.util.PaymentStatus
import com.niyaj.popos.features.reminder.presentation.components.EmployeeSelectionBodyRow
import com.niyaj.popos.features.reminder.presentation.components.EmployeeSelectionFooter
import com.niyaj.popos.features.reminder.presentation.components.EmployeeSelectionHeader
import com.niyaj.popos.features.reminder.presentation.components.InfoCard
import com.niyaj.popos.util.Constants
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun EmployeeAbsentReminderScreen(
    navController : NavController = rememberNavController(),
    scaffoldState : ScaffoldState = rememberScaffoldState(),
    viewModel : AbsentReminderViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    val employees = viewModel.employees.collectAsStateWithLifecycle().value.employees
    val isLoading = viewModel.employees.collectAsStateWithLifecycle().value.isLoading
    val hasError = viewModel.employees.collectAsStateWithLifecycle().value.error

    val selectedDate = viewModel.selectedDate.collectAsStateWithLifecycle().value

    val selectedEmployees = viewModel.selectedEmployees

    val listState = rememberLazyListState()
    val hideBottomBar = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (employees.isEmpty()) {
            resultBackNavigator.navigateBack("Employees not found, add new employee.")
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
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
            AnimatedVisibility(
                visible = !hideBottomBar.value,
                enter = slideInVertically(
                    initialOffsetY = { it }
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background)
                        .padding(bottom = SpaceSmall, top = SpaceMini, start = SpaceMedium, end = SpaceMedium)
                ) {
                    AnimatedVisibility(
                        visible = selectedEmployees.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ){
                        InfoCard(
                            text = Constants.ABSENT_REMINDER_NOTE,
                            isLoading = isLoading
                        )
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
            }
        },
    ) {
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                CircularProgressIndicator()
            }
        }else if (hasError != null) {
            ItemNotAvailable(
                modifier = Modifier.fillMaxWidth(),
                text = hasError
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSmall)
            ) {
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

                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    backgroundColor = LightColor6,
                ) {
                    LazyColumn(
                        state = listState
                    ) {
                        val groupedBy = employees.sortedBy { it.paymentStatus.order }.groupBy { it.paymentStatus }

                        groupedBy.forEach { (paymentStatus, groupedReminderEmployee) ->
                            if (paymentStatus != PaymentStatus.NotPaid) {
                                stickyHeader {
                                    TextWithBorderCount(
                                        modifier = Modifier,
                                        text = paymentStatus.status,
                                        leadingIcon = paymentStatus.icon,
                                        count = groupedReminderEmployee.size,
                                    )
                                }
                            }

                            itemsIndexed(groupedReminderEmployee) { index, reminderEmployee ->

                                val isEnabled = when (reminderEmployee.paymentStatus) {
                                    PaymentStatus.NotPaid -> true
                                    else -> false
                                }

                                EmployeeSelectionBodyRow(
                                    primaryText = reminderEmployee.employee.employeeName,
                                    secText = reminderEmployee.employee.employeePhone,
                                    secIcon = Icons.Default.PhoneAndroid,
                                    isSelected = selectedEmployees.contains(reminderEmployee.employee.employeeId),
                                    paymentStatus = reminderEmployee.paymentStatus,
                                    isEnabled = isEnabled,
                                    onSelectEmployee = {
                                        viewModel.onEvent(AbsentReminderEvent.SelectEmployee(reminderEmployee.employee.employeeId))
                                    }
                                )

                                if (index != employees.size - 1) {
                                    Divider(modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}