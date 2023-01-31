package com.niyaj.popos.features.employee.presentation.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.LightColor6
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ExtendedFabButton
import com.niyaj.popos.features.components.IconBox
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.PaymentStatusChip
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardOutlinedChip
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.destinations.AddEditAbsentScreenDestination
import com.niyaj.popos.features.destinations.AddEditEmployeeScreenDestination
import com.niyaj.popos.features.destinations.AddEditSalaryScreenDestination
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.EMPLOYEE_DETAILS_SCREEN
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags.REMAINING_AMOUNT_TEXT
import com.niyaj.popos.features.employee.domain.util.PaymentType
import com.niyaj.popos.features.reports.presentation.components.SalaryDateDropdown
import com.niyaj.popos.util.Constants.PAID
import com.niyaj.popos.util.toDate
import com.niyaj.popos.util.toFormattedDate
import com.niyaj.popos.util.toFormattedDateAndTime
import com.niyaj.popos.util.toRupee
import com.niyaj.popos.util.toSalaryDate
import com.niyaj.popos.util.toYearAndMonth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
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

    val employee = employeeDetailsViewModel.employeeDetails.collectAsStateWithLifecycle().value.employee
    val isLoading = employeeDetailsViewModel.employeeDetails.collectAsStateWithLifecycle().value.isLoading
    val error = employeeDetailsViewModel.employeeDetails.collectAsStateWithLifecycle().value.error

    val salaries = employeeDetailsViewModel.salaries.collectAsStateWithLifecycle().value.payments
    val salariesIsLoading = employeeDetailsViewModel.salaries.collectAsStateWithLifecycle().value.isLoading
    val salariesHasError = employeeDetailsViewModel.salaries.collectAsStateWithLifecycle().value.error

    val salaryDates = employeeDetailsViewModel.salaryDates.collectAsStateWithLifecycle().value.dates
    val selectedSalaryDate = employeeDetailsViewModel.selectedSalaryDate.value

    val paymentDetail = employeeDetailsViewModel.paymentDetails.collectAsStateWithLifecycle().value.payments
    val paymentDetailError = employeeDetailsViewModel.paymentDetails.collectAsStateWithLifecycle().value.error

    val absentReports = employeeDetailsViewModel.absentReports.collectAsStateWithLifecycle().value.absents
    val absentReportsIsLoading = employeeDetailsViewModel.absentReports.collectAsStateWithLifecycle().value.isLoading
    val absentReportsHasError = employeeDetailsViewModel.absentReports.collectAsStateWithLifecycle().value.error

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
            ExtendedFabButton(
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
                                    text = selectedSalaryDate?.first?.toYearAndMonth
                                        ?: if (salaryDates.isNotEmpty()) salaryDates.first().startDate.toYearAndMonth else "",
                                    salaryDates = salaryDates,
                                    onDateClick = {
                                        employeeDetailsViewModel.onEvent(
                                            EmployeeDetailsEvent.OnChooseSalaryDate(it)
                                        )
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Divider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            if (paymentDetailError == null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = paymentDetail.remainingAmount.toRupee,
                                        style = MaterialTheme.typography.h5,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.testTag(REMAINING_AMOUNT_TEXT)
                                    )

                                    Column(
                                        horizontalAlignment = Alignment.End,
                                    ) {
                                        PaymentStatusChip(
                                            text = paymentDetail.status,
                                            isSelected = paymentDetail.status == PAID
                                        )

                                        if (!paymentDetail.message.isNullOrEmpty()) {
                                            Spacer(modifier = Modifier.height(SpaceSmall))
                                            Text(
                                                text = paymentDetail.message,
                                                color = if (paymentDetail.status == PAID) MaterialTheme.colors.error else MaterialTheme.colors.primary,
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
                                        onClick = {
                                            scope.launch {
                                                lazyListState.animateScrollToItem(3)
                                            }
                                        },
                                        backgroundColor = LightColor6,
                                        modifier = Modifier.testTag("AdvancePayment")
                                    ) {
                                        Text(
                                            text = "${paymentDetail.paymentCount} Advance Payment",
                                            style = MaterialTheme.typography.body2,
                                            modifier = Modifier.padding(SpaceSmall)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(SpaceSmall))
                                    Card(
                                        onClick = {
                                            scope.launch {
                                                lazyListState.animateScrollToItem(4)
                                            }
                                        },
                                        backgroundColor = LightColor6,
                                        modifier = Modifier.testTag("DaysAbsent")
                                    ) {
                                        Text(
                                            text = "${paymentDetail.absentCount} Days Absent",
                                            style = MaterialTheme.typography.body2,
                                            modifier = Modifier.padding(SpaceSmall)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(SpaceSmall))
                                Divider(modifier = Modifier.fillMaxWidth())
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                
                                Button(
                                    onClick = {
                                        navController.navigate(AddEditAbsentScreenDestination(employeeId = employeeId))
                                    },
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
                                    onClick = {
                                        navController.navigate(AddEditSalaryScreenDestination(employeeId = employeeId))
                                    },
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
                                    text = paymentDetailError,
                                    textAlign = TextAlign.Center,
                                )
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))
                }

                item(key = "EmployeeDetails") {
                    Card(
                        modifier = Modifier
                            .testTag("EmployeeDetails")
                            .fillMaxWidth()
                            .clickable {
                                employeeDetailsExpanded = !employeeDetailsExpanded
                            },
                        shape = RoundedCornerShape(4.dp),
                        elevation = SpaceMini
                    ) {
                        StandardExpandable(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall),
                            expanded = employeeDetailsExpanded,
                            onExpandChanged = {
                                employeeDetailsExpanded = !employeeDetailsExpanded
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
                                    onClick = {
                                        navController.navigate(AddEditEmployeeScreenDestination(
                                            employeeId))
                                    }
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
                                    onClick = {
                                        employeeDetailsExpanded = !employeeDetailsExpanded
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
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceSmall)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator()
                                    } else if (error != null) {
                                        ItemNotAvailable(
                                            text = error
                                        )
                                    } else {
                                        TextWithIcon(
                                            modifier = Modifier.testTag(employee.employeeName),
                                            text = "Name - ${employee.employeeName}",
                                            icon = Icons.Default.Person
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            modifier = Modifier.testTag(employee.employeePhone),
                                            text = "Phone - ${employee.employeePhone}",
                                            icon = Icons.Default.PhoneAndroid
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            modifier = Modifier.testTag(employee.employeeSalary.toRupee),
                                            text = "Salary - ${employee.employeeSalary.toRupee}",
                                            icon = ImageVector.vectorResource(id = R.drawable.round_currency_rupee_24)
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            modifier = Modifier.testTag(employee.employeeSalaryType),
                                            text = "Salary Type - ${employee.employeeSalaryType}",
                                            icon = Icons.Default.Merge
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            modifier = Modifier.testTag(employee.employeePosition),
                                            text = "Position - ${employee.employeePosition}",
                                            icon = Icons.Default.Approval
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            modifier = Modifier.testTag(employee.employeeType),
                                            text = "Type - ${employee.employeeType}",
                                            icon = Icons.Default.MergeType
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            modifier = Modifier.testTag(employee.employeeJoinedDate.toDate),
                                            text = "Joined Date : ${employee.employeeJoinedDate.toSalaryDate}",
                                            icon = Icons.Default.CalendarToday
                                        )
                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        TextWithIcon(
                                            text = "Created At : ${employee.createdAt.toFormattedDateAndTime}",
                                            icon = Icons.Default.AccessTime
                                        )
                                        if (employee.updatedAt != null) {
                                            Spacer(modifier = Modifier.height(SpaceSmall))
                                            TextWithIcon(
                                                text = "Updated At : ${employee.updatedAt!!.toFormattedDateAndTime}",
                                                icon = Icons.Default.Login
                                            )
                                        }
                                    }
                                }
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))
                }

                item(key = "PaymentDetails") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                paymentDetailsExpanded = !paymentDetailsExpanded
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
                                paymentDetailsExpanded = !paymentDetailsExpanded
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
                                    onClick = {
                                        navController.navigate(AddEditSalaryScreenDestination(
                                            employeeId = employeeId))
                                    }
                                )
                            },
                            rowClickable = true,
                            expand = { modifier: Modifier ->
                                IconButton(
                                    modifier = modifier,
                                    onClick = {
                                        paymentDetailsExpanded = !paymentDetailsExpanded
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
                                if (salariesIsLoading) {
                                    CircularProgressIndicator()
                                } else if (salaries.isEmpty() || salariesHasError != null) {
                                    ItemNotAvailable(
                                        text = salariesHasError
                                            ?: "You have not paid any amount to this employee."
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(SpaceSmall))

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        salaries.forEachIndexed { index, salary ->
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(SpaceSmall),
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = "${salary.startDate.toFormattedDate} - ${salary.endDate.toFormattedDate}",
                                                            fontWeight = FontWeight.Bold,
                                                        )
                                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                                        Text(
                                                            text = salary.payments.sumOf { it.employeeSalary.toLong() }
                                                                .toString().toRupee,
                                                            fontWeight = FontWeight.SemiBold,
                                                        )
                                                    }

                                                    Column(
                                                        horizontalAlignment = Alignment.End,
                                                    ) {
                                                        PaymentStatusChip(
                                                            text = salary.status,
                                                            isSelected = salary.status == PAID
                                                        )

                                                        if (!salary.message.isNullOrEmpty()) {
                                                            Spacer(modifier = Modifier.height(
                                                                SpaceSmall
                                                            ))
                                                            Text(
                                                                text = salary.message,
                                                                color = if (salary.status == PAID) MaterialTheme.colors.error else MaterialTheme.colors.primary,
                                                                style = MaterialTheme.typography.caption,
                                                                textAlign = TextAlign.End
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(SpaceSmall))
                                                Divider(modifier = Modifier.fillMaxWidth())
                                                Spacer(modifier = Modifier.height(SpaceSmall))

                                                salary.payments.forEachIndexed { index, payment ->
                                                    Row(
                                                        modifier = Modifier
                                                            .testTag(payment.employee?.employeeName.plus(payment.employeeSalary))
                                                            .fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically,
                                                    ) {
                                                        Text(
                                                            text = payment.employeeSalary.toRupee,
                                                            style = MaterialTheme.typography.body1,
                                                            textAlign = TextAlign.Start,
                                                            fontWeight = FontWeight.SemiBold,
                                                            modifier = Modifier
                                                                .weight(0.8F),
                                                        )

                                                        Text(
                                                            text = payment.salaryGivenDate.toSalaryDate,
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
                                                                text = payment.salaryPaymentType,
                                                                icon = when (payment.salaryPaymentType) {
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
                                                                text = payment.salaryType,
                                                            )
                                                        }

                                                    }

                                                    if (index != salary.payments.size - 1) {
                                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                                        Divider(modifier = Modifier.fillMaxWidth())
                                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                                    }
                                                }
                                            }

                                            if (index != salaries.size - 1) {
                                                Spacer(modifier = Modifier.height(SpaceMini))
                                                Divider(modifier = Modifier.fillMaxWidth())
                                                Spacer(modifier = Modifier.height(SpaceMini))
                                            }
                                        }
                                    }
                                }
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))
                }

                item(key = "AbsentDetails") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                absentReportsExpanded = !absentReportsExpanded
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
                                absentReportsExpanded = !absentReportsExpanded
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
                                    onClick = {
                                        navController.navigate(
                                            AddEditAbsentScreenDestination(employeeId = employeeId)
                                        )
                                    }
                                )
                            },
                            rowClickable = true,
                            expand = { modifier: Modifier ->
                                IconButton(
                                    modifier = modifier,
                                    onClick = {
                                        absentReportsExpanded = !absentReportsExpanded
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "Expand Absent Details",
                                        tint = MaterialTheme.colors.secondary
                                    )
                                }
                            },
                            content = {
                                if (absentReportsIsLoading) {
                                    CircularProgressIndicator()
                                } else if (absentReports.isEmpty() || absentReportsHasError != null) {
                                    ItemNotAvailable(
                                        text = absentReportsHasError ?: "Employee absent reports not available"
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(SpaceSmall))

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        absentReports.forEachIndexed { index, absentReport ->
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
                                                        .padding(SpaceMedium),
                                                    crossAxisSpacing = SpaceMini,
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

                                            if (index != absentReports.size - 1) {
                                                Spacer(modifier = Modifier.height(SpaceMini))
                                                Divider(modifier = Modifier.fillMaxWidth())
                                                Spacer(modifier = Modifier.height(SpaceMini))
                                            }
                                        }
                                    }
                                }
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))
                }
            }
        }
    }
}