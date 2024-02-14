package com.niyaj.feature.employee.details

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_DETAILS_SCREEN
import com.niyaj.common.utils.toYearAndMonth
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.employee.components.AbsentDetails
import com.niyaj.feature.employee.components.EmployeeDetails
import com.niyaj.feature.employee.components.PaymentDetails
import com.niyaj.feature.employee.components.SalaryEstimationCard
import com.niyaj.feature.employee.destinations.AddEditEmployeeScreenDestination
import com.niyaj.ui.components.StandardScaffold
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.OpenResultRecipient
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch


/**
 * Employee Details Screen
 * @author Sk Niyaj Ali
 * @param employeeId
 * @param navController
 * @param scaffoldState
 * @param viewModel
 * @param resultRecipient
 * @see EmployeeDetailsViewModel
 */
@Destination
@Composable
fun EmployeeDetailsScreen(
    employeeId: String,
    onClickAddPayment: (String) -> Unit,
    onClickAddAbsent: (String) -> Unit,
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: EmployeeDetailsViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditEmployeeScreenDestination, String>,
    paymentRecipient: OpenResultRecipient<String>,
    absentRecipient: OpenResultRecipient<String>
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val empId =
        navController.currentBackStackEntryAsState().value?.arguments?.getString("employeeId")
            ?: employeeId

    val salaryEstimationState = viewModel.salaryEstimation.collectAsStateWithLifecycle().value

    val salaryDates = viewModel.salaryDates.collectAsStateWithLifecycle().value

    val selectedSalaryDate = viewModel.selectedSalaryDate.value

    val employeeState = viewModel.employeeDetails.collectAsStateWithLifecycle().value

    val paymentsState = viewModel.payments.collectAsStateWithLifecycle().value

    val absentState = viewModel.employeeAbsentDates.collectAsStateWithLifecycle().value

    if (employeeId.isEmpty()) {
        navController.navigateUp()
    }

    var employeeDetailsExpanded by remember {
        mutableStateOf(false)
    }

    var paymentDetailsExpanded by remember {
        mutableStateOf(false)
    }

    var absentReportsExpanded by remember {
        mutableStateOf(false)
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    paymentRecipient.onNavResult {
        when(it) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                viewModel.onEvent(EmployeeDetailsEvent.RefreshData)
            }
        }
    }

    absentRecipient.onNavResult {
        when(it) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                viewModel.onEvent(EmployeeDetailsEvent.RefreshData)
            }
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        navigationIcon = {},
        showBackArrow = true,
        onBackButtonClick = {
            navController.navigateUp()
        },
        navActions = {
            IconButton(
                onClick = {
                    onClickAddPayment(empId)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Payment Entry")
            }

            IconButton(
                onClick = {
                    onClickAddAbsent(empId)
                }
            ) {
                Icon(imageVector = Icons.Default.EventBusy, contentDescription = "Add Absent Entry")
            }
        },
        title = {
            Text(
                text = "Employee Details",
                modifier = Modifier.testTag(EMPLOYEE_DETAILS_SCREEN)
            )
        },
        isFloatingActionButtonDocked = false,
        floatingActionButton = {},
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            state = lazyListState,
        ) {
            item(key = "CalculateSalary") {
                SalaryEstimationCard(
                    uiState = salaryEstimationState,
                    dropdownText = selectedSalaryDate?.first?.toYearAndMonth
                        ?: if (salaryDates.isNotEmpty()) salaryDates.first().startDate.toYearAndMonth else "",
                    salaryDates = salaryDates,
                    onDateClick = {
                        viewModel.onEvent(
                            EmployeeDetailsEvent.OnChooseSalaryDate(it)
                        )
                    },
                    onClickPaymentCount = {
                        scope.launch {
                            lazyListState.animateScrollToItem(3)
                        }
                    },
                    onClickAbsentCount = {
                        scope.launch {
                            lazyListState.animateScrollToItem(4)
                        }
                    },
                    onClickAbsentEntry = {
                        onClickAddAbsent(empId)
                    },
                    onClickSalaryEntry = {
                        onClickAddPayment(empId)
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            item(key = "EmployeeDetails") {
                EmployeeDetails(
                    employeeState = employeeState,
                    employeeDetailsExpanded = employeeDetailsExpanded,
                    onClickEdit = {
                        navController.navigate(AddEditEmployeeScreenDestination(empId))
                    },
                    onExpanded = {
                        employeeDetailsExpanded = !employeeDetailsExpanded
                    }
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            item(key = "PaymentDetails") {
                PaymentDetails(
                    uiState = paymentsState,
                    paymentDetailsExpanded = paymentDetailsExpanded,
                    onExpanded = {
                        paymentDetailsExpanded = !paymentDetailsExpanded
                    }
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            item(key = "AbsentDetails") {
                AbsentDetails(
                    absentState = absentState,
                    absentReportsExpanded = absentReportsExpanded,
                    onExpanded = {
                        absentReportsExpanded = !absentReportsExpanded
                    }
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }
        }
    }

}