package com.niyaj.popos.features.employee_salary.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.niyaj.popos.features.components.ExtendedFabButton
import com.niyaj.popos.features.components.IconBox
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardOutlinedChip
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.StandardSearchBar
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.destinations.AddEditAbsentScreenDestination
import com.niyaj.popos.features.destinations.AddEditSalaryScreenDestination
import com.niyaj.popos.features.employee.domain.util.PaymentType
import com.niyaj.popos.util.toRupee
import com.niyaj.popos.util.toSalaryDate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun SalaryScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    salaryViewModel: SalaryViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditSalaryScreenDestination, String>,
) {

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dialogState = rememberMaterialDialogState()

    val salaries = salaryViewModel.salaries.collectAsState().value.salary
    val isLoading = salaryViewModel.salaries.collectAsState().value.isLoading
    val hasError = salaryViewModel.salaries.collectAsState().value.hasError

    val groupedByEmployeeSalaries = salaries.groupBy { it.employee }

    val totalAmount = salaries.sumOf { it.employeeSalary.toLong() }.toString()
    val employeeCount = groupedByEmployeeSalaries.keys.size.toString()
    val paymentsCount = salaries.size.toString()

    val selectedSalary = salaryViewModel.selectedSalary.collectAsState().value

    val selectedEmployee = salaryViewModel.selectedEmployee.collectAsState().value

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

    val showSearchBar = salaryViewModel.toggledSearchBar.collectAsState().value

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = true) {
        salaryViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    Timber.d(event.successMessage)
                    scaffoldState.snackbarHostState.showSnackbar(event.successMessage)

                }

                is UiEvent.OnError -> {
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


    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        title = {
            if (selectedSalary.isEmpty()){
                Text(text = "Payment Details")
            }
        },
        showBackArrow = true,
        onBackButtonClick = {
            if (showSearchBar) {
                salaryViewModel.onSearchBarCloseAndClearClick()
            } else {
                navController.navigateUp()
            }
        },
        isFloatingActionButtonDocked = salaries.isNotEmpty(),
        floatingActionButton = {
            ExtendedFabButton(
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
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = salaryViewModel.searchText.collectAsState().value,
                    placeholderText = "Search for salaries...",
                    onSearchTextChanged = {
                        salaryViewModel.onEvent(SalaryEvent.OnSearchSalary(it))
                    },
                    onClearClick = {
                        salaryViewModel.onSearchTextClearClick()
                    },
                )
            } else if(selectedSalary.isNotEmpty()) {
            IconButton(
                onClick = {
                    navController.navigate(AddEditSalaryScreenDestination(salaryId = selectedSalary))
                },
            ){
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Salary Item",
                    tint = MaterialTheme.colors.onPrimary,
                )
            }

            IconButton(
                onClick = {
                    dialogState.show()
                },
                enabled = selectedSalary.isNotEmpty()
            ){
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Salary",
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
        } else {
                if (salaries.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            salaryViewModel.onEvent(SalaryEvent.ToggleSearchBar)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search_icon),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if(selectedSalary.isNotEmpty()) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSmall),
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
                    ) {

                        item(key = "totalPayments") {
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
                                            onClick = {
                                                scope.launch {
                                                    lazyListState.animateScrollToItem(4)
                                                }
                                            },
                                            backgroundColor = LightColor6,
                                        ) {
                                            Text(
                                                text = "$paymentsCount Payments",
                                                style = MaterialTheme.typography.body2,
                                                modifier = Modifier.padding(SpaceSmall)
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
                                        )

                                        Card(
                                            onClick = {
                                                scope.launch {
                                                    lazyListState.animateScrollToItem(4)
                                                }
                                            },
                                            backgroundColor = LightColor6,
                                        ) {
                                            Text(
                                                text = "$employeeCount Employees",
                                                style = MaterialTheme.typography.body2,
                                                modifier = Modifier.padding(SpaceSmall)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    Divider(modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    Spacer(modifier = Modifier.height(SpaceSmall))

                                    Button(
                                        onClick = {
                                            navController.navigate(
                                                AddEditAbsentScreenDestination())
                                        },
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

                        item(key = "employeePayments") {
                            groupedByEmployeeSalaries.forEach { (employee, employeeSalaries) ->
                                if(employee != null){
                                    Card(
                                        onClick = {
                                            salaryViewModel.onEvent(
                                                SalaryEvent.SelectEmployee(employee.employeeId)
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(4.dp),
                                        elevation = SpaceMini
                                    ) {
                                        StandardExpandable(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(SpaceSmall),
                                            expanded = selectedEmployee == employee.employeeId,
                                            onExpandChanged = {
                                                salaryViewModel.onEvent(
                                                    SalaryEvent.SelectEmployee(employee.employeeId)
                                                )
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
                                                        navController.navigate(
                                                            AddEditSalaryScreenDestination(employeeId = employee.employeeId)
                                                        )
                                                    }
                                                )
                                            },
                                            rowClickable = true,
                                            expand = { modifier: Modifier ->
                                                IconButton(
                                                    modifier = modifier,
                                                    onClick = {
                                                        salaryViewModel.onEvent(
                                                            SalaryEvent.SelectEmployee(employee.employeeId)
                                                        )
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
                                                employeeSalaries.forEachIndexed { index, salary ->
                                                    Card(
                                                        onClick = {
                                                            salaryViewModel.onEvent(
                                                                SalaryEvent.SelectSalary(salary.salaryId)
                                                            )
                                                        },
                                                        modifier = Modifier
                                                            .fillMaxWidth(),
                                                        elevation = if (selectedSalary == salary.salaryId) 2.dp else 0.dp,
                                                        backgroundColor = if (selectedSalary == salary.salaryId) LightColor6 else MaterialTheme.colors.surface,
                                                        border = if (selectedSalary == salary.salaryId) BorderStroke(1.dp, MaterialTheme.colors.primary) else null,
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
                                                                text = salary.salaryGivenDate.toSalaryDate,
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


                                                    if (index != employeeSalaries.size - 1) {
                                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                                        Divider(modifier = Modifier.fillMaxWidth())
                                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                                    }
                                                }
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(SpaceMedium))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}