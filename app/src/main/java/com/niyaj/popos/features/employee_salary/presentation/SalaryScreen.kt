package com.niyaj.popos.features.employee_salary.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.LightColor6
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.IconBox
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.ScaffoldNavActions
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardOutlinedChip
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.destinations.AddEditAbsentScreenDestination
import com.niyaj.popos.features.destinations.AddEditSalaryScreenDestination
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.util.PaymentType
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.utils.toBarDate
import com.niyaj.popos.utils.toRupee
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Salary Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param salaryViewModel
 * @param resultRecipient
 * @see SalaryViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun SalaryScreen(
    navController : NavController,
    scaffoldState : ScaffoldState,
    salaryViewModel : SalaryViewModel = hiltViewModel(),
    resultRecipient : ResultRecipient<AddEditSalaryScreenDestination, String>,
) {

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dialogState = rememberMaterialDialogState()

    val salaries = salaryViewModel.salaries.collectAsStateWithLifecycle().value.salary
    val isLoading = salaryViewModel.salaries.collectAsStateWithLifecycle().value.isLoading
    val hasError = salaryViewModel.salaries.collectAsStateWithLifecycle().value.hasError

    val groupedByEmployeeSalaries = salaries.groupBy { it.employee?.employeeId }

    val totalAmount = salaries.sumOf { it.employeeSalary.toLong() }.toString()
    val employeeCount = groupedByEmployeeSalaries.keys.size
    val paymentsCount = salaries.size

    val selectedSalary = salaryViewModel.selectedSalary.collectAsStateWithLifecycle().value

    val selectedEmployee = salaryViewModel.selectedEmployee.collectAsStateWithLifecycle().value

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedSalary.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if (isContextualMode) {
            MaterialTheme.colors.secondary
        } else {
            MaterialTheme.colors.primary
        }
    }

    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if (isContextualMode) {
            MaterialTheme.colors.secondary
        } else {
            MaterialTheme.colors.primary
        }
    }

    val showSearchBar = salaryViewModel.toggledSearchBar.collectAsStateWithLifecycle().value

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = true) {
        salaryViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    Timber.d(event.successMessage)
                    scaffoldState.snackbarHostState.showSnackbar(event.successMessage)

                }

                is UiEvent.Error -> {
                    Timber.d(event.errorMessage)
                    scaffoldState.snackbarHostState.showSnackbar(event.errorMessage)
                }

                is UiEvent.IsLoading -> {
                    Timber.d("Loading.. ${event.isLoading.toString()}")
                }
            }
        }
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

    BackHandler(true) {
        if (showSearchBar) {
            salaryViewModel.onSearchBarCloseAndClearClick()
        } else if (selectedSalary.isNotEmpty()) {
            salaryViewModel.onEvent(
                SalaryEvent.SelectSalary(selectedSalary)
            )
        } else {
            navController.navigateUp()
        }
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    SentryTraced(tag = "SalaryScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            title = {
                if (selectedSalary.isEmpty()) {
                    Text(text = "Payment Details")
                }
            },
            showBackArrow = selectedSalary.isEmpty(),
            onBackButtonClick = {
                if (showSearchBar) {
                    salaryViewModel.onSearchBarCloseAndClearClick()
                } else {
                    navController.navigateUp()
                }
            },
            isFloatingActionButtonDocked = salaries.isNotEmpty(),
            floatingActionButton = {
                StandardFabButton(
                    text = stringResource(id = R.string.create_salary_entry).uppercase(),
                    showScrollToTop = showScrollToTop.value,
                    visible = salaries.isNotEmpty() && selectedSalary.isEmpty() && !showSearchBar,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(index = 0)
                        }
                    },
                    onClick = {
                        navController.navigate(AddEditSalaryScreenDestination())
                    },
                )
            },
            floatingActionButtonPosition = if (showScrollToTop.value) FabPosition.End else FabPosition.Center,
            navActions = {
                ScaffoldNavActions(
                    multiSelect = false,
                    allItemsIsEmpty = salaries.isEmpty(),
                    selectedItem = selectedSalary,
                    onClickEdit = {
                        navController.navigate(AddEditSalaryScreenDestination(salaryId = selectedSalary))
                    },
                    onClickDelete = {
                        dialogState.show()
                    },
                    showSearchBar = showSearchBar,
                    searchText = "",
                    onSearchTextChanged = {
                        salaryViewModel.onEvent(SalaryEvent.OnSearchSalary(it))
                    },
                    onClearClick = {
                        salaryViewModel.onSearchTextClearClick()
                    },
                    onClickSearch = {
                        salaryViewModel.onEvent(SalaryEvent.ToggleSearchBar)
                    }
                )
            },
            navigationIcon = {
                if (selectedSalary.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            salaryViewModel.onEvent(SalaryEvent.SelectSalary(selectedSalary))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close_icon),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            },
            topAppBarBackgroundColor = backgroundColor,
        ) {
            MaterialDialog(
                dialogState = dialogState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            salaryViewModel.onEvent(SalaryEvent.DeleteSalary(selectedSalary))
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            dialogState.hide()
                            salaryViewModel.onEvent(SalaryEvent.SelectSalary(selectedSalary))
                        },
                    )
                }
            ) {
                title(text = "Delete Employee Salary?")
                message(res = R.string.delete_salary_msg)
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    salaryViewModel.onEvent(SalaryEvent.RefreshSalary)
                }
            ) {
                if (salaries.isEmpty() || hasError != null) {
                    ItemNotAvailable(
                        text = hasError
                            ?: if (showSearchBar)
                                stringResource(id = R.string.search_item_not_found)
                            else stringResource(id = R.string.no_items_in_salary),
                        buttonText = stringResource(id = R.string.create_salary_entry).uppercase(),
                        onClick = {
                            navController.navigate(AddEditSalaryScreenDestination())
                        }
                    )
                } else {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.padding(SpaceSmall)
                    ) {

                        item(key = "totalPayments") {
                            TotalPayment(
                                totalAmount = totalAmount,
                                paymentsCount = paymentsCount,
                                employeesCount = employeeCount,
                                onClickEmployeeCount = {
                                    scope.launch {
                                        lazyListState.animateScrollToItem(1)
                                    }
                                },
                                onClickTotalPayments = {
                                    scope.launch {
                                        lazyListState.animateScrollToItem(1)
                                    }
                                },
                                onClickAbsentEntry = {
                                    navController.navigate(AddEditAbsentScreenDestination())
                                }
                            )
                        }

                        item(key = "employeePayments") {
                            groupedByEmployeeSalaries.forEach { (employeeId, employeeSalaries) ->
                                employeeId?.let { empId ->
                                    val data = salaryViewModel.getEmployeeById(empId)

                                    data?.let { employee ->
                                        EmployeePayments(
                                            employee = employee,
                                            employeeSalaries = employeeSalaries,
                                            selectedSalary = selectedSalary,
                                            isExpanded = selectedEmployee == empId,
                                            onSelectSalary = {
                                                salaryViewModel.onEvent(
                                                    SalaryEvent.SelectSalary(it)
                                                )
                                            },
                                            onSelectEmployee = {
                                                salaryViewModel.onEvent(
                                                    SalaryEvent.SelectEmployee(it)
                                                )
                                            } ,
                                            onExpandChanged = {
                                                salaryViewModel.onEvent(
                                                    SalaryEvent.SelectEmployee(it)
                                                )
                                            },
                                            onClickAddSalaryBtn = {
                                                navController.navigate(
                                                    AddEditSalaryScreenDestination(employeeId = it)
                                                )
                                            },
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                }
                            }
                        }
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
fun TotalPayment(
    paymentsCount : Int = 0,
    employeesCount : Int = 0,
    onClickEmployeeCount : () -> Unit,
    onClickTotalPayments : () -> Unit,
    totalAmount : String = "0",
    onClickAbsentEntry : () -> Unit,
) {
    Spacer(modifier = Modifier.height(SpaceSmall))

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Total Payments",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    onClick = onClickTotalPayments,
                    backgroundColor = LightColor6,
                    modifier = Modifier.testTag("TotalPayments")
                ) {
                    Text(
                        text = "$paymentsCount Payments",
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier
                            .padding(SpaceSmall)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = totalAmount.toRupee,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("TotalAmount")
                )

                Card(
                    onClick = onClickEmployeeCount,
                    backgroundColor = LightColor6,
                    modifier = Modifier
                        .testTag("TotalEmployees")
                ) {
                    Text(
                        text = "$employeesCount Employees",
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier
                            .padding(SpaceSmall)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))
            Spacer(modifier = Modifier.height(SpaceSmall))

            Button(
                onClick = onClickAbsentEntry,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(ButtonSize),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondaryVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = "Add Absent Entry",
                )
                Spacer(modifier = Modifier.width(SpaceMini))
                Text(
                    text = "Add Absent Entry".uppercase(),
                    style = MaterialTheme.typography.button,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }

    Spacer(modifier = Modifier.height(SpaceMedium))
}


/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmployeePayments(
    employee: Employee,
    employeeSalaries : List<EmployeeSalary>,
    selectedSalary : String = "",
    onSelectSalary : (String) -> Unit,
    onSelectEmployee: (String) -> Unit,
    isExpanded: Boolean = false,
    onExpandChanged: (String) -> Unit,
    onClickAddSalaryBtn: (String) -> Unit
) {
    Card(
        onClick = {
            onSelectEmployee(employee.employeeId)
        },
        modifier = Modifier
            .testTag(employee.employeeName.plus("Tag"))
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = isExpanded,
            onExpandChanged = {
                onExpandChanged(employee.employeeId)
            },
            title = {
                TextWithIcon(
                    text = employee.employeeName,
                    icon = Icons.Default.Person,
                    isTitle = true
                )
            },
            trailing = {
                IconBox(
                    text = "Add Entry",
                    icon = Icons.Default.Add,
                    onClick = {
                        onClickAddSalaryBtn(employee.employeeId)
                    }
                )
            },
            rowClickable = true,
            expand = { modifier : Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onSelectEmployee(employee.employeeId)
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
                EmployeePaymentsData(
                    employeeSalaries = employeeSalaries,
                    selectedSalary = selectedSalary,
                    onSelectSalary = onSelectSalary
                )
            }
        )
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmployeePaymentsData(
    employeeSalaries: List<EmployeeSalary>,
    onSelectSalary: (String) -> Unit,
    selectedSalary: String = "",
) {
    employeeSalaries.forEachIndexed { index, salary ->
        Card(
            onClick = {
                onSelectSalary(salary.salaryId)
            },
            modifier = Modifier
                .testTag(
                    salary.employee?.employeeName.plus(
                        salary.employeeSalary
                    )
                )
                .fillMaxWidth(),
            elevation = if (selectedSalary == salary.salaryId) 2.dp else 0.dp,
            backgroundColor = if (selectedSalary == salary.salaryId) LightColor6 else MaterialTheme.colors.surface,
            border = if (selectedSalary == salary.salaryId) BorderStroke(
                1.dp,
                MaterialTheme.colors.primary
            ) else null,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = salary.employeeSalary.toRupee,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(0.8F),
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

                    Spacer(
                        modifier = Modifier.width(
                            SpaceSmall
                        )
                    )

                    StandardOutlinedChip(
                        text = salary.salaryType,
                    )
                }

            }
        }

        if (index != employeeSalaries.size - 1) {
            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}