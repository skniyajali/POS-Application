package com.niyaj.popos.features.employee.presentation.details

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.R
import com.niyaj.popos.common.utils.Constants.PAID
import com.niyaj.popos.common.utils.toBarDate
import com.niyaj.popos.common.utils.toDate
import com.niyaj.popos.common.utils.toFormattedDate
import com.niyaj.popos.common.utils.toFormattedDateAndTime
import com.niyaj.popos.common.utils.toRupee
import com.niyaj.popos.common.utils.toSalaryDate
import com.niyaj.popos.common.utils.toYearAndMonth
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.LightColor6
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.IconBox
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.PaymentStatusChip
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardOutlinedChip
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.destinations.AddEditAbsentScreenDestination
import com.niyaj.popos.features.destinations.AddEditEmployeeScreenDestination
import com.niyaj.popos.features.destinations.AddEditSalaryScreenDestination
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_DETAILS_SCREEN
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.REMAINING_AMOUNT_TEXT
import com.niyaj.popos.features.employee.domain.util.PaymentType
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.employee_salary.domain.util.SalaryCalculableDate
import com.niyaj.popos.features.reports.presentation.components.SalaryDateDropdown
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.launch

/**
 * Employee Details Screen
 * @author Sk Niyaj Ali
 * @param employeeId
 * @param navController
 * @param scaffoldState
 * @param employeeDetailsViewModel
 * @param resultRecipient
 * @param addEditSalaryRecipient
 * @param addEditAbsentRecipient
 * @see EmployeeDetailsViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun EmployeeDetailsScreen(
    employeeId: String,
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    employeeDetailsViewModel: EmployeeDetailsViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditEmployeeScreenDestination, String>,
    addEditSalaryRecipient: ResultRecipient<AddEditSalaryScreenDestination, String>,
    addEditAbsentRecipient: ResultRecipient<AddEditAbsentScreenDestination, String>
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val employeeState = employeeDetailsViewModel.employeeDetails.collectAsStateWithLifecycle().value
    val isLoading = employeeDetailsViewModel.employeeDetails.collectAsStateWithLifecycle().value.isLoading

    val salariesState = employeeDetailsViewModel.salaries.collectAsStateWithLifecycle().value

    val salaryDates = employeeDetailsViewModel.salaryDates.collectAsStateWithLifecycle().value.dates
    val selectedSalaryDate = employeeDetailsViewModel.selectedSalaryDate.value

    val paymentDetailState = employeeDetailsViewModel.paymentDetails.collectAsStateWithLifecycle().value

    val absentState = employeeDetailsViewModel.absentReports.collectAsStateWithLifecycle().value

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

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
                    employeeDetailsViewModel.onEvent(EmployeeDetailsEvent.RefreshEmployeeDetails)
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    addEditSalaryRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    employeeDetailsViewModel.onEvent(EmployeeDetailsEvent.RefreshEmployeeDetails)
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    addEditAbsentRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    employeeDetailsViewModel.onEvent(EmployeeDetailsEvent.RefreshEmployeeDetails)
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    SentryTraced(tag = "EmployeeDetailsScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = true,
            onBackButtonClick = {
                navController.navigateUp()
            },
            title = {
                Text(
                    text = "Employee Details",
                    modifier = Modifier.testTag(EMPLOYEE_DETAILS_SCREEN)
                )
            },
            isFloatingActionButtonDocked = false,
            floatingActionButton = {
                StandardFabButton(
                    text = stringResource(id = R.string.create_new_employee).uppercase(),
                    showScrollToTop = showScrollToTop.value,
                    visible = false,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(index = 0)
                        }
                    },
                    onClick = {
                        navController.navigate(AddEditEmployeeScreenDestination())
                    },
                )
            },
            floatingActionButtonPosition = if (showScrollToTop.value) FabPosition.End else FabPosition.Center,
            navActions = {
                IconButton(
                    onClick = {
                        navController.navigate(AddEditSalaryScreenDestination(employeeId = employeeId))
                    }
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Payment Entry")
                }

                IconButton(
                    onClick = {
                        navController.navigate(AddEditAbsentScreenDestination(employeeId = employeeId))
                    }
                ) {
                    Icon(imageVector = Icons.Default.EventBusy, contentDescription = "Add Absent Entry")
                }
            },
            navigationIcon = {},
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    employeeDetailsViewModel.onEvent(EmployeeDetailsEvent.RefreshEmployeeDetails)
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                    state = lazyListState,
                ) {
                    item(key = "CalculateSalary") {
                        SalaryEstimationCard(
                            state = paymentDetailState,
                            dropdownText = selectedSalaryDate?.first?.toYearAndMonth ?: if (salaryDates.isNotEmpty()) salaryDates.first().startDate.toYearAndMonth else "",
                            salaryDates = salaryDates,
                            onDateClick = {
                                employeeDetailsViewModel.onEvent(
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
                                navController.navigate(AddEditAbsentScreenDestination(employeeId = employeeId))
                            },
                            onClickSalaryEntry = {
                                navController.navigate(AddEditSalaryScreenDestination(employeeId = employeeId))
                            },
                        )

                        Spacer(modifier = Modifier.height(SpaceMedium))
                    }

                    item(key = "EmployeeDetails") {
                        EmployeeDetails(
                            employeeState = employeeState,
                            employeeDetailsExpanded = employeeDetailsExpanded,
                            onClickEdit = {
                                navController.navigate(AddEditEmployeeScreenDestination(employeeId))
                            },
                            onExpanded = {
                                employeeDetailsExpanded = !employeeDetailsExpanded
                            }
                        )

                        Spacer(modifier = Modifier.height(SpaceMedium))
                    }

                    item(key = "PaymentDetails") {
                        PaymentDetails(
                            salariesState = salariesState,
                            paymentDetailsExpanded = paymentDetailsExpanded,
                            onExpanded = {
                                paymentDetailsExpanded = !paymentDetailsExpanded
                            },
                            onClickPaymentEntry = {
                                navController.navigate(AddEditSalaryScreenDestination(employeeId))
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
                            },
                            onClickAbsentEntry = {
                                navController.navigate(
                                    AddEditAbsentScreenDestination(employeeId = employeeId)
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(SpaceMedium))
                    }
                }
            }
        }
    }
}


/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SalaryEstimationCard(
    state: EmployeePaymentState,
    dropdownText: String = "",
    salaryDates: List<SalaryCalculableDate> = emptyList(),
    onDateClick: (Pair<String, String>) -> Unit = {},
    onClickPaymentCount: () -> Unit = {},
    onClickAbsentCount: () -> Unit = {},
    onClickAbsentEntry: () -> Unit = {},
    onClickSalaryEntry: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .testTag("CalculateSalary")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Salary Estimation",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                SalaryDateDropdown(
                    text = dropdownText,
                    salaryDates = salaryDates,
                    onDateClick = {
                        onDateClick(it)
                    }
                )
            }
            Spacer(modifier = Modifier.height(SpaceSmall))
            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))

            if (state.error == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = state.payments.remainingAmount.toRupee,
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag(REMAINING_AMOUNT_TEXT)
                    )

                    Column(
                        horizontalAlignment = Alignment.End,
                    ) {
                        PaymentStatusChip(isPaid = state.payments.status == PAID)

                        state.payments.message?.let {
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Text(
                                text = it,
                                color = if (state.payments.status == PAID) MaterialTheme.colors.error else MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.caption,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
                Divider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceSmall))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Card(
                        onClick = onClickPaymentCount,
                        backgroundColor = LightColor6,
                        modifier = Modifier.testTag("AdvancePayment")
                    ) {
                        Text(
                            text = "${state.payments.paymentCount} Advance Payment",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(SpaceSmall)
                        )
                    }
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Card(
                        onClick = onClickAbsentCount,
                        backgroundColor = LightColor6,
                        modifier = Modifier.testTag("DaysAbsent")
                    ) {
                        Text(
                            text = "${state.payments.absentCount} Days Absent",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(SpaceSmall)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
                Divider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceSmall))

                Button(
                    onClick = onClickAbsentEntry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(ButtonSize),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = LightColor6,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = "Add Absent Entry Button",
                        tint = MaterialTheme.colors.error
                    )
                    Spacer(modifier = Modifier.width(SpaceMini))
                    Text(
                        text = "Add Absent Entry".uppercase(),
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.error
                    )
                }

                Spacer(modifier = Modifier.height(SpaceSmall))

                Button(
                    onClick = onClickSalaryEntry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(ButtonSize),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondaryVariant,
                    )
                ) {
                    Icon(imageVector = Icons.Default.Money, contentDescription = "Add Payment Entry Button" )
                    Spacer(modifier = Modifier.width(SpaceMini))
                    Text(
                        text = "Add Payment Entry".uppercase(),
                        style = MaterialTheme.typography.button,
                    )
                }

            } else {
                Text(
                    text = state.error,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}


/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmployeeDetails(
    employeeState: EmployeeDetailsState,
    employeeDetailsExpanded: Boolean = false,
    onClickEdit: () -> Unit = {},
    onExpanded: () -> Unit = {}
) {
    Card(
        onClick = onExpanded,
        modifier = Modifier
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
                Crossfade(targetState = employeeState, label = "EmployeeDetailsState") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.employee != null -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall)
                            ) {
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.employee.employeeName),
                                    text = "Name - ${state.employee.employeeName}",
                                    icon = Icons.Default.Person
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.employee.employeePhone),
                                    text = "Phone - ${state.employee.employeePhone}",
                                    icon = Icons.Default.PhoneAndroid
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.employee.employeeSalary.toRupee),
                                    text = "Salary - ${state.employee.employeeSalary.toRupee}",
                                    icon = Icons.Default.CurrencyRupee
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.employee.employeeSalaryType),
                                    text = "Salary Type - ${state.employee.employeeSalaryType}",
                                    icon = Icons.Default.Merge
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.employee.employeePosition),
                                    text = "Position - ${state.employee.employeePosition}",
                                    icon = Icons.Default.Approval
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.employee.employeeType),
                                    text = "Type - ${state.employee.employeeType}",
                                    icon = Icons.Default.MergeType
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.employee.employeeJoinedDate.toDate),
                                    text = "Joined Date : ${state.employee.employeeJoinedDate.toSalaryDate}",
                                    icon = Icons.Default.CalendarToday
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    text = "Created At : ${state.employee.createdAt.toFormattedDateAndTime}",
                                    icon = Icons.Default.AccessTime
                                )
                                state.employee.updatedAt?.let {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    TextWithIcon(
                                        text = "Updated At : ${it.toFormattedDateAndTime}",
                                        icon = Icons.Default.Login
                                    )
                                }
                            }
                        }
                        else -> {
                            ItemNotAvailable(
                                text = employeeState.error ?: "Employee details not found",
                                showImage = false,
                            )
                        }
                    }
                }
            },
        )
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PaymentDetails(
    salariesState: EmployeeSalaryState,
    paymentDetailsExpanded: Boolean = false,
    onExpanded : () -> Unit = {},
    onClickPaymentEntry: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onExpanded()
            }
            .testTag("PaymentDetails"),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = paymentDetailsExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                TextWithIcon(
                    text = "Payment Details",
                    icon = Icons.Default.Money,
                    isTitle = true
                )
            },
            trailing = {
                IconBox(
                    text = "Add Entry",
                    icon = Icons.Default.Add,
                    onClick = onClickPaymentEntry
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpanded()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                Crossfade(targetState = salariesState, label = "PaymentDetails") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.payments.isNotEmpty() -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                state.payments.forEachIndexed { index, salaries ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(SpaceSmall),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Column {
                                            Text(
                                                text = "${salaries.startDate.toFormattedDate} - ${salaries.endDate.toFormattedDate}",
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.height(SpaceSmall))
                                            Text(
                                                text = salaries.payments.sumOf { it.employeeSalary.toLong() }
                                                    .toString().toRupee,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        Divider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        salaries.payments.forEachIndexed { index, salary ->
                                            EmployeePayment(salary = salary)

                                            if (index != salaries.payments.size - 1) {
                                                Spacer(modifier = Modifier.height(SpaceSmall))
                                                Divider(modifier = Modifier.fillMaxWidth())
                                                Spacer(modifier = Modifier.height(SpaceSmall))
                                            }
                                        }
                                    }

                                    if (index != state.payments.size - 1) {
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        Divider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                    }
                                }
                            }
                        }
                        else -> {
                            ItemNotAvailable(
                                text = state.error ?: "You have not paid any amount to this employee.",
                                showImage = false,
                            )
                        }
                    }
                }
            },
        )
    }
}


@Composable
fun EmployeePayment(
    salary: EmployeeSalary,
) {
    Row(
        modifier = Modifier
            .testTag(
                salary.employee?.employeeName.plus(
                    salary.employeeSalary
                )
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = salary.employeeSalary.toRupee,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.8F)
        )

        Text(
            text = salary.salaryGivenDate.toBarDate,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(0.8F),
        )

        Row(
            modifier = Modifier.weight(1.4F),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            IconBox(
                text = salary.salaryPaymentType,
                icon = when (salary.salaryPaymentType) {
                    PaymentType.Cash.paymentType -> Icons.Default.Money
                    PaymentType.Online.paymentType -> Icons.Default.AccountBalance
                    else -> Icons.Default.Payments
                },
                selected = false,
            )

            Spacer(modifier = Modifier.width(
                SpaceSmall
            ))

            StandardOutlinedChip(
                text = salary.salaryType,
            )
        }
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun AbsentDetails(
    absentState: MonthlyAbsentReportState,
    absentReportsExpanded: Boolean = false,
    onExpanded : () -> Unit,
    onClickAbsentEntry : () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onExpanded()
            }
            .testTag("AbsentDetails"),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = absentReportsExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                TextWithIcon(
                    text = "Absent Details",
                    icon = Icons.Default.EventBusy,
                    isTitle = true
                )
            },
            trailing = {
                IconBox(
                    text = "Add Entry",
                    icon = Icons.Default.Add,
                    onClick = onClickAbsentEntry
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand Absent Details",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                Crossfade(targetState = absentState, label = "AbsentDetails") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()

                        state.absents.isNotEmpty() -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                state.absents.forEachIndexed { index, absentReport ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(SpaceSmall),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Column {
                                            Text(
                                                text = "${absentReport.startDate.toFormattedDate} - ${absentReport.endDate.toFormattedDate}",
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.height(SpaceSmall))
                                            Text(
                                                text = "${absentReport.absent.size} Days Absent",
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        Divider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        FlowRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(SpaceSmall),
                                        ) {
                                            absentReport.absent.forEach{  attendance ->
                                                Card(
                                                    backgroundColor = LightColor6,
                                                    modifier = Modifier
                                                        .testTag(attendance.employee?.employeeName.plus(attendance.absentDate.toDate))
                                                ) {
                                                    Text(
                                                        text = attendance.absentDate.toFormattedDate,
                                                        style = MaterialTheme.typography.body1,
                                                        textAlign = TextAlign.Start,
                                                        fontWeight = FontWeight.SemiBold,
                                                        modifier = Modifier
                                                            .padding(SpaceSmall)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(SpaceSmall))
                                            }
                                        }
                                    }

                                    if (index != state.absents.size - 1) {
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        Divider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                    }
                                }
                            }
                        }

                        else -> {
                            ItemNotAvailable(
                                text = state.error ?: "Employee absent reports not available"
                            )
                        }
                    }
                }
            },
        )
    }
}