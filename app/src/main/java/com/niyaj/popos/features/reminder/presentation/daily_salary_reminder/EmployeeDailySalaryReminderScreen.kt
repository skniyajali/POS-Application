package com.niyaj.popos.features.reminder.presentation.daily_salary_reminder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardIconButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithBorderCount
import com.niyaj.popos.features.components.TopBarTitle
import com.niyaj.popos.features.destinations.AddEditEmployeeScreenDestination
import com.niyaj.popos.features.reminder.domain.util.PaymentStatus
import com.niyaj.popos.features.reminder.presentation.components.EmployeeSelectionBodyRow
import com.niyaj.popos.features.reminder.presentation.components.EmployeeSelectionFooter
import com.niyaj.popos.features.reminder.presentation.components.EmployeeSelectionHeader
import com.niyaj.popos.features.reminder.presentation.components.InfoCard
import com.niyaj.popos.utils.Constants
import com.niyaj.popos.utils.Constants.SALARY_HOST
import com.niyaj.popos.utils.Constants.SALARY_HOST_SECURE
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.ResultBackNavigator
import io.sentry.compose.SentryTraced

/**
 * Daily Salary Reminder Screen
 * @author SK Niyaj Ali
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Destination(
    deepLinks = [
        DeepLink(uriPattern = SALARY_HOST_SECURE),
        DeepLink(uriPattern = SALARY_HOST)
    ]
)
@Composable
fun EmployeeDailySalaryReminderScreen(
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: DailySalaryReminderViewModel = hiltViewModel(),
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

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    SentryTraced(tag = "EmployeeDailySalaryReminderScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = true,
            showBottomBar = true,
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
                    text = "Mark Paid Employee",
                )
            },
            bottomBar = {
                if (employees.isNotEmpty()) {
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
                                InfoCard(text = Constants.DAILY_SALARY_REMINDER_NOTE)
                            }

                            EmployeeSelectionFooter(
                                primaryText = "Mark As Paid",
                                onPrimaryClick = {
                                    viewModel.onEvent(DailySalaryReminderEvent.MarkAsPaid)
                                },
                                onSecondaryClick = {
                                    navController.navigateUp()
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            },
        ) {
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            } else if (employees.isEmpty() || hasError != null) {
                ItemNotAvailable(
                    modifier = Modifier.fillMaxWidth(),
                    text = hasError ?: "Employees not found!",
                    buttonText = "Create new employee",
                    onClick = {
                        navController.navigate(AddEditEmployeeScreenDestination())
                    }
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
                            viewModel.onEvent(DailySalaryReminderEvent.SelectDate(it))
                        },
                        onCheckedChange = {
                            viewModel.onEvent(DailySalaryReminderEvent.SelectAllEmployee)
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
                            val groupedBy = employees.sortedBy { it.paymentStatus.order }
                                .groupBy { it.paymentStatus }

                            groupedBy.forEach { (paymentStatus, groupedReminderEmployee) ->
                                stickyHeader {
                                    TextWithBorderCount(
                                        modifier = Modifier,
                                        text = paymentStatus.status,
                                        leadingIcon = paymentStatus.icon,
                                        count = groupedReminderEmployee.size,
                                    )
                                }

                                itemsIndexed(groupedReminderEmployee) { index, reminderEmployee ->

                                    val isEnabled = when (reminderEmployee.paymentStatus) {
                                        PaymentStatus.NotPaid -> true
                                        else -> false
                                    }

                                    EmployeeSelectionBodyRow(
                                        primaryText = reminderEmployee.employee.employeeName,
                                        secText = reminderEmployee.employeeSalary,
                                        secIcon = Icons.Default.Money,
                                        isSelected = selectedEmployees.contains(reminderEmployee.employee.employeeId),
                                        paymentStatus = reminderEmployee.paymentStatus,
                                        isEnabled = isEnabled,
                                        onSelectEmployee = {
                                            viewModel.onEvent(
                                                DailySalaryReminderEvent.SelectEmployee(
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
                        }
                    }
                }
            }
        }
    }
}